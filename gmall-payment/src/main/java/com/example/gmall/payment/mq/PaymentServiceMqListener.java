package com.example.gmall.payment.mq;

import com.example.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

@Component
public class PaymentServiceMqListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumePaymentCheckResult(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        Integer count = 0;
        if (mapMessage.getString("count") != null) {
            count = Integer.parseInt("" + mapMessage.getString("count"));
        }
        // 调用paymentService的支付宝检查接口
        System.out.println("进行延迟检查，调用支付检查的接口服务");
        Map<String, Object> resultMap = paymentService.checkAlipayPayment(out_trade_no);

        if (resultMap != null && resultMap.isEmpty()) {
            String trade_status = (String) resultMap.get("trade_status");

            // 根据查询的支付状态结果，判断接下来要进行的操作
            if ("TRADE_SUCCESS".equals(trade_status)) {
                // 支付成功，更新支付发送支付队列
                paymentService.updatePayment(null);
                System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
                return;
            }
        }

        if (count > 0){
            // 继续发送延迟检查任务，计算延迟时间等
            System.out.println("没有支付成功，检查剩余次数为" + count + "继续发现延迟检查任务");
            count--;
            paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
        }else{
            System.out.println("检查剩余次数用尽，结束检查任务");
        }
    }
}
