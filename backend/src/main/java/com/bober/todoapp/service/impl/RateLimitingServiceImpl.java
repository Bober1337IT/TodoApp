package com.bober.todoapp.service.impl;

import com.bober.todoapp.config.RateLimitConfig;
import com.bober.todoapp.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class RateLimitingServiceImpl implements RateLimitingService {

    private final Map<String, Bucket> rateLimitCache;

    public RateLimitingServiceImpl(Map<String, Bucket> rateLimitCache) {
        this.rateLimitCache = rateLimitCache;
    }

    @Override
    public Bucket resolveBucket(String key) {
        return resolveBucket(key, RateLimitConfig::createNewBucket);
    }

    private Bucket resolveBucket(String key, Supplier<Bucket> factory) {
        return rateLimitCache.computeIfAbsent(key, ignored -> factory.get());
    }

    @Override
    public boolean isAllowed(String key) {
        return resolveBucket(key).tryConsume(1);
    }

    @Override
    public boolean isTaskWriteAllowed(String key) {
        return resolveBucket(key, RateLimitConfig::createTaskWriteBucket).tryConsume(1);
    }

    @Override
    public boolean isTaskStatusAllowed(String key) {
        return resolveBucket(key, RateLimitConfig::createTaskStatusBucket).tryConsume(1);
    }
}
