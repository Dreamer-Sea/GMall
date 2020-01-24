package com.example.gmall.cart.service.impl;

import com.example.gmall.bean.OmsCartItem;
import com.example.gmall.service.CartService;

public class CartServiceImpl implements CartService {
    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        return null;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {

    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {

    }

    @Override
    public void flushCartCache(String memberId) {

    }
}
