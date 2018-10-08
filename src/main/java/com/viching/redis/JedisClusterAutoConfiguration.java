package com.viching.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.viching.redis.aspect.PullCacheAspect;
import com.viching.redis.aspect.PushCacheAspect;
import com.viching.redis.aspect.RemoveCacheAspect;
import com.viching.redis.util.RedisObjectSerializer;

/**
 * jedis cluster config
 * @project viching-redis-cache
 * @author Administrator	
 * @date 2018年8月25日
 * Copyright (C) 2016-2018 www.viching.com Inc. All rights reserved.
 */
@Configuration
@ConditionalOnClass({RedisTemplate.class})
@EnableConfigurationProperties(RedisClusterConfig.class)
@Import({PullCacheAspect.class, PushCacheAspect.class, RemoveCacheAspect.class})
public class JedisClusterAutoConfiguration {
    
    //自动注入redis配置属性文件
    @Autowired
    private RedisClusterConfig rcConfig;
    
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisClusterConfiguration config = new RedisClusterConfiguration(rcConfig.getCluster().getNodes());
        config.setMaxRedirects(rcConfig.getCluster().getMaxRedirects());
        config.setPassword(RedisPassword.of(rcConfig.getPassword()));
        return new JedisConnectionFactory(config);
    }
 
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }
}
