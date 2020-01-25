package com.example.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.example.gmall.bean.OmsCartItem;
import com.example.gmall.bean.PmsSkuInfo;
import com.example.gmall.service.CartService;
import com.example.gmall.service.SkuService;
import com.example.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String userId = "1";

        if (StringUtils.isNotBlank(userId)){
            // 已经登录查询db
            omsCartItems = cartService.cartList(userId);
        }else {
            // 没有登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
        
        modelMap.put("cartList", omsCartItems);
        return "cartList";
    }

    @RequestMapping("addToCart")
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response){

        // 调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId, "");

        // 将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));

        // 判断用户是否登录
        String memberId = "1";

        if (StringUtils.isBlank(memberId)){
            // 用户没有登录
            List<OmsCartItem> omsCartItems = new ArrayList<>();
            // cookie里原有的购物数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);

            if (StringUtils.isBlank(cartListCookie)){
                // cookie为空
                omsCartItems.add(omsCartItem);
            } else {
                // cookie不为空
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);

                // 判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);

                if (exist){
                    // 之前添加过，更新购物车添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                        }
                    }

                } else {
                    // 之前没有田间，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }

            // 更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60*60*72, true);
        }else{
            // 用户登录了
            List<OmsCartItem> omsCartItems = new ArrayList<>();
            // 从db中查出购物车数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);
            if (omsCartItemFromDb == null){
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("test小明");
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);
            } else {
                // 该用户添加过当前商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }
            // 同步缓存
            cartService.flushCartCache(memberId);
        }

        return "redirect:/success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            if (productSkuId.equals(omsCartItem.getProductId())){
                b = true;
            }
        }
        return b;
    }

    @RequestMapping("testRedirect")
    public String testRedirect(){

        return "redirect:/success.html";
    }
}
