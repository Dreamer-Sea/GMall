package com.example.gmall.manage.redissionTest;

import com.example.gmall.util.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

@Controller
public class RedissionController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("testRedission")
    public String testRedission(){

        Jedis jedis = redisUtil.getJedis();

        RLock lock = redissonClient.getLock("lock");

        return null;
    }
}
