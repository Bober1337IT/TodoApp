package com.bober.todoapp.service;

import io.github.bucket4j.Bucket;

public interface RateLimitingService {

    Bucket resolveBucket(String key);

    boolean isAllowed(String key);
}
