package com.example.gmall.service;

import com.example.gmall.bean.OmsOrder;

import java.math.BigDecimal;

public interface OrderService {

    String checkTradeCode(String memberId, String tradeCode);

    String genTradeCode(String memberId);

    void saveOrder(OmsOrder omsOrder);
}
