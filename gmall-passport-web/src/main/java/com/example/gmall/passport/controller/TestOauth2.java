package com.example.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.example.gmall.util.HttpclientUtil;

import java.util.Map;

public class TestOauth2 {

    public static void main(String[] args) {

        // App Key: 751468148

        // App Secret: 8c95e615bcde11a4c255ba78967b57cb

        // 授权回调地址: http://passport.gmall.com:8085/vlogin

        // 授权取消地址: http://passport.gmall.com:8085/vlogout

//        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=751468148&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");

        String s2 = "http://passport.gmall.com:8085/vlogin?code=3fc572ca3a9bf3d61f96b78e4e247612";

        String s3 = "https://api.weibo.com/oauth2/access_token?client_id=751468148&client_secret=8c95e615bcde11a4c255ba78967b57cb&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=3fc572ca3a9bf3d61f96b78e4e247612";

        String access_token_json = HttpclientUtil.doPost(s3, null);

        Map<String, String> access_map = JSON.parseObject(access_token_json, Map.class);

        System.out.println(access_map.get("access_token"));
    }

}
