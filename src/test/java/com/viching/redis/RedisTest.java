package com.viching.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppTest.class}, properties = "classpath:application.yml")
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test1() {
        redisTemplate.opsForValue().set("abc", "123456");
        String result = redisTemplate.opsForValue().get("abc");
        System.out.println("result>>>" + result);
    }
}
