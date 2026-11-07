package com.bober.todoapp.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    @Bean
    public Map<String, Bucket> rateLimitCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public BucketConfiguration bucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(defaultBandwidth())
                .build();
    }

    public static Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(defaultBandwidth())
                .build();
    }

    private static Bandwidth defaultBandwidth() {
        return Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
    }
}
