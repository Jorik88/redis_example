package com.example.redis_example.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.redis.config")
@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {

    private int redisTtlSec;

    @Autowired
    private Environment env;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(env.getProperty("spring.redis.host"));
        redisConf.setPort(Integer.parseInt(env.getProperty("spring.redis.port")));
        redisConf.setPassword(RedisPassword.of(env.getProperty("spring.redis.password")));
        return new LettuceConnectionFactory(redisConf);
    }

    @Bean(name = "firstConfiguration")
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(20))
                .disableCachingNullValues();
    }

    @Bean(name = "firstManager")
    public RedisCacheManager cacheManager() {
        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfiguration())
                .transactionAware()
                .build();
    }

    @Bean(name = "secondConfiguration")
    public RedisCacheConfiguration secondCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(redisTtlSec))
                .disableCachingNullValues();
    }

    @Bean(name = "secondManager")
    public RedisCacheManager secondCacheManager() {
        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(secondCacheConfiguration())
                .transactionAware()
                .build();
    }
}
