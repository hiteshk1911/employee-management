package com.hitesh.employeemanagement.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RateLimitConfig {

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(
                "localhost",
                6379
        );
    }

    @Bean
    public ProxyManager<byte[]> proxyManager(
            JedisPool jedisPool) {

        return JedisBasedProxyManager
                .builderFor(jedisPool)
                .build();
    }
}