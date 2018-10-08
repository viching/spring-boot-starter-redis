package com.viching.redis.aspect;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.viching.redis.annotation.RemoveCache;
import com.viching.redis.util.ReflectTools;

@Aspect
@Component
public class RemoveCacheAspect {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.viching.redis.annotation.RemoveCache)")
    private void pointcut(){}
    
    @SuppressWarnings({ "unchecked" })
    @Around("pointcut()")
    public Object save(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //获取方法参数
        List<Object> list = ReflectTools.adapter(joinPoint, RemoveCache.class);
        RemoveCache annotation = (RemoveCache) list.get(0);

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
        
        final String key1 = annotation.value();
        final String key2 = field;
        if(key1.equals("")){
            redisTemplate.opsForValue().getOperations().delete(key2);
        }else{
            redisTemplate.opsForHash().delete(key1, key2);
        }
        //执行方法本身
        result = joinPoint.proceed();
        return result;
    }
}
