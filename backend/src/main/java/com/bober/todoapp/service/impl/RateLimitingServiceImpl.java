package com.bober.todoapp.service.impl;

import com.bober.todoapp.config.RateLimitConfig;
import com.bober.todoapp.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RateLimitingServiceImpl implements RateLimitingService {

    private final Map<String, Bucket> rateLimitCache;

    public RateLimitingServiceImpl(Map<String, Bucket> rateLimitCache) {
        this.rateLimitCache = rateLimitCache;
    }

    @Override
    public Bucket resolveBucket(String key) {
        return rateLimitCache.computeIfAbsent(key, ignored -> RateLimitConfig.createNewBucket());
    }

    @Override
    public boolean isAllowed(String key) {
        return resolveBucket(key).tryConsume(1);
    }
}
