package com.hitesh.employeemanagement.security;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final ProxyManager<byte[]> proxyManager;

    public boolean allowLoginRequest(String ip) {

        Bucket bucket = proxyManager.builder()
                .build(
                        key("login:" + ip),
                        () -> BucketConfiguration.builder()
                                .addLimit(Bandwidth.classic(
                                        5,
                                        Refill.intervally(
                                                5,
                                                Duration.ofMinutes(1)
                                        )
                                ))
                                .build()
                );

        return bucket.tryConsume(1);
    }

    public boolean allowRegisterRequest(String ip) {

        Bucket bucket = proxyManager.builder()
                .build(
                        key("register:" + ip),
                        () -> BucketConfiguration.builder()
                                .addLimit(Bandwidth.classic(
                                        3,
                                        Refill.intervally(
                                                3,
                                                Duration.ofMinutes(1)
                                        )
                                ))
                                .build()
                );

        return bucket.tryConsume(1);
    }

    public boolean allowEmployeeRequest(String username) {

        Bucket bucket = proxyManager.builder()
                .build(
                        key("employee:" + username),
                        () -> BucketConfiguration.builder()
                                .addLimit(Bandwidth.classic(
                                        5,
                                        Refill.intervally(
                                                5,
                                                Duration.ofMinutes(1)
                                        )
                                ))
                                .build()
                );

        return bucket.tryConsume(1);
    }

    private byte[] key(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}