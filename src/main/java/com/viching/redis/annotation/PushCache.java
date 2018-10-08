package com.viching.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PushCache {
    
    String value() default ""; //缓存hset key1

    String key() default ""; //缓存hset key2 或者set key
    
    String target() default ""; //value

    long expires() default 0l; //失效时间
    
    boolean after() default false; //在数据库之前操作还是之后，默认在数据库操作之前
    
    boolean transaction() default false; //是否开启redis事务，乐观锁
}
