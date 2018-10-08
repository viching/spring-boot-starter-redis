package com.viching.redis.aspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.viching.redis.annotation.PullCache;
import com.viching.redis.util.ReflectTools;

@Aspect
@Component
public class PullCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.viching.redis.annotation.PullCache)")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object query(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //获取方法参数
        List<Object> list = ReflectTools.adapter(joinPoint, PullCache.class);
        PullCache annotation = (PullCache) list.get(0);

        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) list.get(1);
        String[] keys = annotation.key().split("\\.");
        String field = null;
        if (keys.length == 1) {
            field = (String) parameters.get(keys[0]);
        } else {
            Object temp = parameters.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                temp = ReflectTools.getFieldValue(temp, keys[i]);
            }
            field = (String) temp;
        }
        //先从redis中获取
        if(annotation.value().equals("")){
            result = redisTemplate.opsForValue().get(field);
        }else{
            result = redisTemplate.opsForHash().get(annotation.value(), field);
        }
        if (result != null) {
            return result;
        }
        
        //执行方法本身
        result = joinPoint.proceed();
        
        if(result != null){
            if(annotation.value().equals("")){
                redisTemplate.opsForValue().set(field, result);
            }else{
                redisTemplate.opsForHash().put(annotation.value(), field, result);
            }
            if (annotation.expires() > 0) {
                redisTemplate.expire(annotation.value(), annotation.expires(), TimeUnit.MINUTES);
            }
        }
        return result;
    }
}
