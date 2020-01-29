package com.example.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.annotations.LoginRequired;
import com.example.gmall.bean.OmsCartItem;
import com.example.gmall.bean.OmsOrderItem;
import com.example.gmall.bean.UmsMemberReceiveAddress;
import com.example.gmall.service.CartService;
import com.example.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 收件人地址列表
        List<UmsMemberReceiveAddress> receiveAddressByMemberId = userService.getReceiveAddressByMemberId(memberId);

        // 将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if ("1".equals(omsCartItem.getIsChecked())){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());

                omsOrderItems.add(omsOrderItem);
            }
        }

        modelMap.put("omsOrderItems", omsOrderItems);

        return "trade";
    }
}
