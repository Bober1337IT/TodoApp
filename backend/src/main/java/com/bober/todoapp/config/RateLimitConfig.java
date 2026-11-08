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
                .addLimit(taskWriteBandwidth())
                .build();
    }

    // Used by auth filter (if re-enabled)
    public static Bucket createNewBucket() {
        return createTaskWriteBucket();
    }

    // POST / PUT / DELETE on tasks — 30/min
    public static Bucket createTaskWriteBucket() {
        return Bucket.builder()
                .addLimit(taskWriteBandwidth())
                .build();
    }

    // PATCH .../status — 60/min
    public static Bucket createTaskStatusBucket() {
        return Bucket.builder()
                .addLimit(taskStatusBandwidth())
                .build();
    }

    private static Bandwidth taskWriteBandwidth() {
        return Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
    }

    private static Bandwidth taskStatusBandwidth() {
        return Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
    }
}
