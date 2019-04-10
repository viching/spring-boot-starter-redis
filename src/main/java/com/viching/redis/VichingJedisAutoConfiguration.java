package com.viching.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.viching.redis.aspect.PullCacheAspect;
import com.viching.redis.aspect.PushCacheAspect;
import com.viching.redis.aspect.RemoveCacheAspect;
import com.viching.redis.util.RedisObjectSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * jedis cluster config
 *
 * @author viching
 * @project viching-redis-cache
 * @date 2018年8月25日
 * Copyright (C) 2016-2018 www.viching.com Inc. All rights reserved.
 */
@Configuration
@ConditionalOnClass({RedisTemplate.class})
@EnableConfigurationProperties(VichingRedisConfig.class)
@Import({PullCacheAspect.class, PushCacheAspect.class, RemoveCacheAspect.class})
public class VichingJedisAutoConfiguration {

    //自动注入redis配置属性文件
    @Autowired
    private VichingRedisConfig vichingRedisConfig;

    @Bean
    @ConditionalOnMissingBean
    public JedisConnectionFactory jedisConnectionFactory() {
        if (vichingRedisConfig.getCluster() != null && vichingRedisConfig.getCluster().getNodes() != null && vichingRedisConfig.getCluster().getNodes().size() > 1) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(vichingRedisConfig.getCluster().getNodes());
            config.setMaxRedirects(vichingRedisConfig.getCluster().getMaxRedirects());
            config.setPassword(RedisPassword.of(vichingRedisConfig.getPassword()));
            return new JedisConnectionFactory(config);
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            if (!StringUtils.isEmpty(vichingRedisConfig.getUrl())) {
                String sp[] = vichingRedisConfig.getUrl().split(":");
                config.setHostName(sp[0]);
                config.setPort(Integer.valueOf(sp[1]));
            } else {
                config.setHostName(vichingRedisConfig.getHost());
                config.setPort(vichingRedisConfig.getPort());
            }
            config.setDatabase(vichingRedisConfig.getDatabase());
            config.setPassword(RedisPassword.of(vichingRedisConfig.getPassword()));

            JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
            jedisClientConfiguration.connectTimeout(Duration.ofMillis(vichingRedisConfig.getTimeout()));//  connection timeout

            return new JedisConnectionFactory(config,
                    jedisClientConfiguration.build());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }
}
