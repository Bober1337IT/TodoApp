package com.bober.todoapp.security;

import com.bober.todoapp.service.RateLimitingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//NOT IN USE MIGRATE TO PROXY

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final byte[] TOO_MANY_REQUESTS_BODY =
            "{\"error\":\"Too many requests. Try again later.\"}".getBytes(StandardCharsets.UTF_8);

    private final RateLimitingService rateLimitingService;
    private final MeterRegistry meterRegistry;

    public AuthRateLimitFilter(RateLimitingService rateLimitingService, MeterRegistry meterRegistry) {
        this.rateLimitingService = rateLimitingService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register"));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = resolveClientIp(request);
        String path = request.getRequestURI();
        String key = path + ":" + clientIp;

        if (!rateLimitingService.isAllowed(key)) {
            Counter.builder("auth.rate_limit.rejected")
                    .description("Auth requests rejected by rate limiting")
                    .tag("path", path)
                    .register(meterRegistry)
                    .increment();

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            response.getOutputStream().write(TOO_MANY_REQUESTS_BODY);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
