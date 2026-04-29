package com.hitesh.employeemanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        boolean allowed = true;

        // Auth APIs -> IP based
        if (path.equals("/api/auth/login")) {
            allowed = rateLimitService.allowLoginRequest(ip);
        } else if (path.equals("/api/auth/register")) {
            allowed = rateLimitService.allowRegisterRequest(ip);
        }

        // Employee APIs -> username based
        else if (path.startsWith("/api/employees")) {

            Authentication authentication =
                    SecurityContextHolder.getContext()
                            .getAuthentication();

            if (authentication != null
                    && authentication.isAuthenticated()) {

                Object principal =
                        authentication.getPrincipal();

                String username;

                if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                    username = userDetails.getUsername();
                } else if (principal instanceof com.hitesh.employeemanagement.user.User user) {
                    username = user.getUsername();
                } else {
                    username = authentication.getName();
                }
                System.out.println("Rate limit username = " + username);
                allowed =
                        rateLimitService
                                .allowEmployeeRequest(username);
            }
        }

        if (!allowed) {

            response.setStatus(429);
            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded. Try again later."
                    }
                    """
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(
            HttpServletRequest request) {

        String forwarded =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (forwarded != null &&
                !forwarded.isBlank()) {

            return forwarded.split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}