package com.example.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.example.gmall.bean.OmsOrder;
import com.example.gmall.bean.OmsOrderItem;
import com.example.gmall.mq.ActiveMQUtil;
import com.example.gmall.order.mapper.OmsOrderItemMapper;
import com.example.gmall.order.mapper.OmsOrderMapper;
import com.example.gmall.service.CartService;
import com.example.gmall.service.OrderService;
import com.example.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Reference
    CartService cartService;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {

        Jedis jedis = null;

        try{
            jedis = redisUtil.getJedis();

            String tradeKey = "user:" + memberId + ":tradeCode";
            String tradeCodeFromCache = jedis.get(tradeKey);

            // 对比防重删令牌
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));

            if (eval != null && eval != 0L){
//                jedis.del(tradeKey);
                return "success";
            }
        }finally {
            jedis.close();
        }

        return "fail";
    }

    @Override
    public String genTradeCode(String memberId) {

        Jedis jedis = redisUtil.getJedis();

        String tradeKey = "user:" + memberId + ":tradeCode";

        String tradeCode = UUID.randomUUID().toString();

        jedis.setex(tradeKey, 60 * 15, tradeCode);

        jedis.close();

        return tradeCode;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        // 保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        // 保存订单详情表
        List<OmsOrderItem> omsOrders = omsOrder.getOmsOrders();
        for (OmsOrderItem omsOrderItem : omsOrders) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
        // 删除购物车数据
//        cartService.delCart();
        
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {

        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        return omsOrderMapper.selectOne(omsOrder);
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example e = new Example(OmsOrder.class);
        e.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());

        OmsOrder omsOrderUpdate = new OmsOrder();

        omsOrderUpdate.setStatus("1");

        // 发送一个订单已支付的队列，提供给库存消费
        Connection connection = null;
        Session session = null;
        try{
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            omsOrderMapper.updateByExampleSelective(omsOrderUpdate, e);
            Queue order_pay_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(order_pay_queue);
//            MapMessage mapMessage = new ActiveMQMapMessage();
            TextMessage textMessage = new ActiveMQTextMessage();

            // 查询订单的对象，转化成json字符串，存入ORDER_PAY_QUEUE的消息队列
            OmsOrder omsOrderParam = new OmsOrder();
            omsOrderParam.setOrderSn(omsOrder.getOrderSn());
            OmsOrder omsOrderResponse = omsOrderMapper.selectOne(omsOrderParam);

            OmsOrderItem omsOrderItemParam = new OmsOrderItem();
            omsOrderItemParam.setOrderSn(omsOrderParam.getOrderSn());
            List<OmsOrderItem> select = omsOrderItemMapper.select(omsOrderItemParam);
            omsOrderResponse.setOmsOrders(select);
            textMessage.setText(JSON.toJSONString(omsOrderResponse));

            omsOrderMapper.updateByExampleSelective(omsOrderUpdate, e);
            producer.send(textMessage);
            session.commit();
        }catch (Exception ex){
            ex.printStackTrace();
            try {
                session.rollback();
            } catch (JMSException exc) {
                exc.printStackTrace();
            }
        }finally {
            try {
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }

    }
}
