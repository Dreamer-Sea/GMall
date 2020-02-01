package com.example.gmall.payment;

import com.example.gmall.mq.ActiveMQUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@SpringBootTest
public class GmallPaymentApplicationTests {

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Test
    public void contextLoads() throws JMSException {

        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        Connection connection = connectionFactory.createConnection();

        System.out.println(connection);

    }

}
