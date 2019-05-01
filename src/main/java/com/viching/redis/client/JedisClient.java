package com.viching.redis.client;

import com.viching.redis.util.ReflectTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * Jedis 接口
 * Created by cynic on 2016/8/22.
 */
@Component
public class JedisClient {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 限制最多只有25个线程同时进行
    private ExecutorService pool = Executors.newCachedThreadPool();

    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void set(String key, T value, Integer second) {
        redisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
    }

    public <T> T hget(String hkey, String key) {
        return (T) redisTemplate.opsForHash().get(hkey, key);
    }

    public <T> void hset(String hkey, String key, T value) {
        redisTemplate.opsForHash().put(hkey, key, value);
    }

    public <T> void hset(String hkey, String key, T value, Integer second) {
        redisTemplate.opsForHash().put(hkey, key, value);
        expire(hkey, second);
    }

    long incr(String key) {
        return redisTemplate.opsForValue().increment(key).longValue();
    }

    public void expire(String key, Integer second) {
        redisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    /**
     * 返回给定 key的剩余生存时间(TTL, time to live)。
     *
     * @param key
     * @return
     */
    long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }

    public void hdel(String hkey, String key) {
        redisTemplate.opsForHash().delete(hkey, key);
    }

    /**
     * 将一个值 value插入到列表 key的队列头部
     *
     * @param key
     * @param value
     * @return
     */
    public <T> long lpush(String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从列表 key的队列头部获取一个值
     *
     * @param key 键名
     * @return
     */
    public <T> T lpop(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 将一个值 value插入到列表 key的队列尾部
     *
     * @param key   reids键名
     * @param value 键值
     */
    public <T> long rpush(String key, T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从列表 key的队列尾部获取一个值
     *
     * @param key 键名
     * @return
     */
    public <T> T rpop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 从一个队列的右边弹出一个元素并将这个元素放入另一个指定队列的最左边
     *
     * @param key         reids键名
     * @param destination 键值
     */
    public <T> T rpoplpush(String key, String destination) {
        return (T) redisTemplate.opsForList().rightPopAndLeftPush(key, destination);
    }

    /**
     * 获取队列数据
     *
     * @param key 键名
     * @return
     */
    public <T> List<T> lpopList(String key) {
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }
}
