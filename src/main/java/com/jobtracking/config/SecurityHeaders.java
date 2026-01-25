package com.jobtracking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security headers configuration to protect user data
 */
@Configuration
public class SecurityHeaders implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityHeadersInterceptor());
    }

    public static class SecurityHeadersInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Prevent clickjacking
            response.setHeader("X-Frame-Options", "DENY");
            
            // Prevent MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");
            
            // Enable XSS protection
            response.setHeader("X-XSS-Protection", "1; mode=block");
            
            // Strict transport security (HTTPS only)
            if (request.isSecure()) {
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            }
            
            // Content Security Policy
            response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' https:; " +
                "connect-src 'self'");
            
            // Referrer policy
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Permissions policy
            response.setHeader("Permissions-Policy", 
                "geolocation=(), " +
                "microphone=(), " +
                "camera=(), " +
                "payment=(), " +
                "usb=()");
            
            return true;
        }
    }
}