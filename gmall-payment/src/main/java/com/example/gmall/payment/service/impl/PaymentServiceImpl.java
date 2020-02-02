package com.example.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.gmall.bean.PaymentInfo;
import com.example.gmall.mq.ActiveMQUtil;
import com.example.gmall.payment.mapper.PaymentInfoMapper;
import com.example.gmall.service.PaymentService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        String orderSn = paymentInfo.getOrderSn();

        Example e = new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn", orderSn);

        Connection connection = null;
        Session session = null;
        try{
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        }catch (JMSException jex){
            jex.printStackTrace();
        }

        try{
            paymentInfoMapper.updateByExampleSelective(paymentInfo, e);

            // 支付成功后，引起的系统服务-》订单服务-》库存服务-》物流
            // 调用MQ发送支付成功的信息
            Queue payment_success_queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(payment_success_queue);

            MapMessage mapMessage = new ActiveMQMapMessage();

            mapMessage.setString("out_trade_no", paymentInfo.getOrderSn());

            producer.send(mapMessage);

            session.commit();
        }catch (Exception ex){
            // 消息回滚
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

    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo) {
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payment_check_queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(payment_check_queue);
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("out_trade_no", outTradeNo);
            // 为消息加入延迟的时间
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000*3);
            producer.send(mapMessage);
            session.commit();
        }catch (Exception e){
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
