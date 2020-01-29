package com.example.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.gmall.service.OrderService;
import com.example.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

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
}
