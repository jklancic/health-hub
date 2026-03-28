package com.healthhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.healthhub.repository.UserRepository;
import com.healthhub.entity.User;

import java.io.IOException;
import java.util.UUID;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.extractId(jwt);
                log.debug("JWT token extracted for user ID: {}", userId);
            } catch (Exception e) {
                log.warn("JWT token extraction failed: {}", e.getMessage());
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null || userId == null || userId.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<User> optionalUser = userRepository.findById(userUuid);

            if (optionalUser.isEmpty()) {
                log.warn("User not found for ID: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtil.validateToken(jwt, optionalUser.get())) {
                log.warn("Invalid JWT token for user ID: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            User user = optionalUser.get();
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("User authenticated successfully: {}", user.getUsername());
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for user ID: {}", userId, e);
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
