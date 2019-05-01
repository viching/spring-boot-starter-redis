package com.viching.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppTest.class}, properties = "classpath:application.yml")
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private com.viching.redis.client.JedisClient jedisClient;

    @Test
    public void test1() {
        redisTemplate.opsForValue().set("abc", "123456");
        String result = (String)redisTemplate.opsForValue().get("abc");
        System.out.println("result>>>" + result);
    }

    @Test
    public void test2() {
        Student student = new Student();
        student.setId(1);
        student.setName("zhangsan");
        student.setDescription("是个好姑娘");

        jedisClient.set("user", student);

        Student xx = jedisClient.get("user");

        System.out.println(xx.getDescription());

        jedisClient.del("user");
    }

    @Test
    public void test3() {
        Student student = new Student();
        student.setId(1);
        student.setName("zhangsan");
        student.setDescription("是个好姑娘");

        List<Student> list = new ArrayList<>();
        list.add(student);
        jedisClient.set("user", list);

        List<Student> xx = jedisClient.get("user");

        System.out.println(xx.get(0).getDescription());

        jedisClient.del("user");
    }

    @Test
    public void test4() {
        Student student = new Student();
        student.setId(1);
        student.setName("zhangsan");
        student.setDescription("是个好姑娘");

        jedisClient.lpush("user", student);

        List<Student> xx = jedisClient.lpopList("user");

        System.out.println(xx.get(0).getDescription());

        jedisClient.del("user");
    }

}
