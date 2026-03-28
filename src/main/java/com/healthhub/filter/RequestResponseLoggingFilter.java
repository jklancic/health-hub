package com.healthhub.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter to log all incoming HTTP requests and outgoing responses.
 * Provides detailed information about HTTP method, URI, headers, and response status.
 */
@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 1000; // Limit payload logging to 1000 characters

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request and response to cache content for logging
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            logRequest(wrappedRequest);

            // Continue with the filter chain
            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log response
            logResponse(wrappedResponse, duration);

            // Copy the cached response body to the actual response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== Incoming Request ===\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }
        logMessage.append("\n");

        // Log headers (excluding sensitive ones)
        Map<String, String> headers = getRequestHeaders(request);
        if (!headers.isEmpty()) {
            logMessage.append("Headers: ").append(headers).append("\n");
        }

        // Log request body if present
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String payload = new String(content);
            if (payload.length() > MAX_PAYLOAD_LENGTH) {
                payload = payload.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
            }
            logMessage.append("Body: ").append(payload).append("\n");
        }

        logger.info(logMessage.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== Outgoing Response ===\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");
        logMessage.append("Duration: ").append(duration).append("ms\n");

        // Log response body if present and not too large
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String payload = new String(content);
            if (payload.length() > MAX_PAYLOAD_LENGTH) {
                payload = payload.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
            }
            logMessage.append("Body: ").append(payload).append("\n");
        }

        logMessage.append("========================\n");

        if (response.getStatus() >= 400) {
            logger.error(logMessage.toString());
        } else {
            logger.info(logMessage.toString());
        }
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            // Skip sensitive headers
            if (isSensitiveHeader(headerName)) {
                headers.put(headerName, "***REDACTED***");
            } else {
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        return headers;
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        return lowerHeaderName.contains("authorization") ||
               lowerHeaderName.contains("password") ||
               lowerHeaderName.contains("token") ||
               lowerHeaderName.contains("secret") ||
               lowerHeaderName.contains("api-key");
    }
}
