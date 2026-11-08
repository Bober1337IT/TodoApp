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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class TaskRateLimitFilter extends OncePerRequestFilter {

    private static final byte[] TOO_MANY_REQUESTS_BODY =
            "{\"error\":\"Too many requests. Try again later.\"}".getBytes(StandardCharsets.UTF_8);

    private final RateLimitingService rateLimitingService;
    private final MeterRegistry meterRegistry;

    public TaskRateLimitFilter(RateLimitingService rateLimitingService, MeterRegistry meterRegistry) {
        this.rateLimitingService = rateLimitingService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(isTaskWrite(request) || isTaskStatusPatch(request));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String clientId = resolveClientId();
        if (clientId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean statusPatch = isTaskStatusPatch(request);
        String key = (statusPatch ? "tasks-status:" : "tasks-write:") + clientId;
        boolean allowed = statusPatch
                ? rateLimitingService.isTaskStatusAllowed(key)
                : rateLimitingService.isTaskWriteAllowed(key);

        if (!allowed) {
            Counter.builder("tasks.rate_limit.rejected")
                    .description("Task write/status requests rejected by rate limiting")
                    .tag("method", request.getMethod())
                    .tag("scope", statusPatch ? "status" : "write")
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

    private static boolean isTaskWrite(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return path.startsWith("/api/v1/tasks")
                && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"));
    }

    private static boolean isTaskStatusPatch(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "PATCH".equals(request.getMethod())
                && path.startsWith("/api/v1/tasks/")
                && path.endsWith("/status");
    }

    private String resolveClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal.id().toString();
    }
}
