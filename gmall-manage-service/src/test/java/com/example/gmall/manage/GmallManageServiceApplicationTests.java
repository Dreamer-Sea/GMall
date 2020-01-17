package com.example.gmall.manage;

import com.example.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallManageServiceApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void contextLoads() {
        Jedis jedis = redisUtil.getJedis();
        System.out.println(jedis);
    }

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testRedisson(){

        Jedis jedis = redisUtil.getJedis();

        RLock lock = redissonClient.getLock("lock");

    }
}
