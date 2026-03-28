package com.healthhub.filter;

import com.healthhub.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private final Map<String, Bucket> authCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiCache = new ConcurrentHashMap<>();

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        String key = resolveKey(request);

        Bucket bucket;
        String limitType;

        if (isAuthEndpoint(requestURI)) {
            bucket = authCache.computeIfAbsent(key, k -> createAuthBucket());
            limitType = "AUTH";
        } else {
            bucket = apiCache.computeIfAbsent(key, k -> createApiBucket());
            limitType = "API";
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            logger.warn("Rate limit exceeded for {} - Type: {}, Key: {}, URI: {}",
                       getClientIP(request), limitType, key, requestURI);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
        }
    }

    private boolean isAuthEndpoint(String requestURI) {
        return requestURI != null && requestURI.startsWith("/api/auth/");
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return "user:" + authentication.getName();
        }

        return "ip:" + getClientIP(request);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        String realIP = request.getHeader("X-Real-IP");
        if (realIP != null && !realIP.isEmpty()) {
            return realIP;
        }

        return request.getRemoteAddr();
    }

    private Bucket createAuthBucket() {
        RateLimitProperties.LimitConfig authConfig = rateLimitProperties.getAuth();
        Bandwidth limit = Bandwidth.builder()
                .capacity(authConfig.getCapacity())
                .refillIntervally(authConfig.getCapacity(), Duration.ofMinutes(authConfig.getRefillMinutes()))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createApiBucket() {
        RateLimitProperties.LimitConfig apiConfig = rateLimitProperties.getApi();
        Bandwidth limit = Bandwidth.builder()
                .capacity(apiConfig.getCapacity())
                .refillIntervally(apiConfig.getCapacity(), Duration.ofMinutes(apiConfig.getRefillMinutes()))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
