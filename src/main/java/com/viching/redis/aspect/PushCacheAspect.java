package com.viching.redis.aspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import com.viching.redis.annotation.PushCache;
import com.viching.redis.util.ReflectTools;

@Aspect
@Component
public class PushCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ExecutorService pool = Executors.newCachedThreadPool();

    @Pointcut("@annotation(com.viching.redis.annotation.PushCache)")
    private void pointcut() {
    }

    @SuppressWarnings({ "unchecked" })
    @Around("pointcut()")
    public Object save(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //获取方法参数
        List<Object> list = ReflectTools.adapter(joinPoint, PushCache.class);
        PushCache annotation = (PushCache) list.get(0);

        Map<String, Object> parameters = (Map<String, Object>) list.get(1);
        String[] keys = annotation.key().split("\\.");
        Object field = null;
        if (keys.length == 1) {
            field = (String) parameters.get(keys[0]);
        } else {
            Object temp = parameters.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                temp = ReflectTools.getFieldValue(temp, keys[i]);
            }
            field = temp;
        }

        Object targetObj = null;
        if (annotation.target().equals("")) {
            targetObj = parameters.get(keys[0]);
        } else {
            keys = annotation.key().split("\\.");
            Object temp = parameters.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                temp = ReflectTools.getFieldValue(temp, keys[i]);
            }
            targetObj = temp;
        }

        if (!annotation.after()) {
            saveRedis(annotation.value(), field, targetObj, annotation.expires());
        }

        //执行方法本身
        result = joinPoint.proceed();

        if (annotation.after()) {
            saveRedis(annotation.value(), field, targetObj, annotation.expires());
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void saveRedis(final String key1, final Object key2, final Object value, final long expires) throws Exception {
        //保存至redis
        Object origin = null;
        if (key1.equals("")) {
            origin = redisTemplate.opsForValue().get(key2);
        } else {
            origin = redisTemplate.opsForHash().get(key1, key2);
        }
        Object target = value;
        if (origin != null) {
            target = ReflectTools.combineFieldsCore(origin, value);
        }
        if (key1.equals("")) {
            redisTemplate.opsForValue().set(key2.toString(), target);
        } else {
            redisTemplate.opsForHash().put(key1, key2, target);
        }
        if (expires > 0) {
            if (key1.equals("")) {
                redisTemplate.expire(key1, expires, TimeUnit.MINUTES);
            } else {
                redisTemplate.expire(key2.toString(), expires, TimeUnit.MINUTES);
            }
        }
    }
}
