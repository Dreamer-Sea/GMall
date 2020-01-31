package com.example.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.gmall.bean.PaymentInfo;
import com.example.gmall.payment.mapper.PaymentInfoMapper;
import com.example.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        String orderSn = paymentInfo.getOrderSn();

        Example e = new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn", orderSn);

        paymentInfoMapper.updateByExampleSelective(paymentInfo, e);
    }
}
