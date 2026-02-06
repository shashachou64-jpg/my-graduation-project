package com.cjy;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class CacheTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedisConnection() {
        // 写入
        stringRedisTemplate.opsForValue().set("test:redis", "hello redis");

        // 读取
        String val = stringRedisTemplate.opsForValue().get("test:redis");
        System.out.println("Redis 返回的值：" + val);
    }
}