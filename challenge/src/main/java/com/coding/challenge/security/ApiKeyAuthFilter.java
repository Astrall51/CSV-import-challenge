package com.coding.challenge.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Value("${app.security.api-key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestApiKey = request.getHeader("X-API-KEY");

        if (requestApiKey == null || requestApiKey.isBlank()) {
            log.trace("Incoming request without API key to: {}", request.getRequestURI());
        } else if (validApiKey != null && validApiKey.equals(requestApiKey)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("API-USER", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("API key authentication successful for URI: {}. Key starts with: {}***", request.getRequestURI(), requestApiKey.substring(0, Math.min(requestApiKey.length(), 3)));
        } else {
            log.warn("Invalid API key attempt! Received: {}***, URI: {}, IP: {}", requestApiKey.length() > 3 ? requestApiKey.substring(0, 3) : "short", request.getRequestURI(), request.getRemoteAddr());
        }

        filterChain.doFilter(request, response);
    }
}
