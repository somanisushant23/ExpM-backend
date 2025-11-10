package com.userservice.expmbff.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class HttpInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(HttpInterceptor.class);
    private String requestId;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        String requestId = request.getHeader("requestId");
        if(requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader("requestId", requestId);

        if(shouldSkipAuth(request)) {
            return true;
        } else {
            String authHeader = request.getHeader("Authorization");
            String headerEmail = request.getHeader("email");
            String token = extractToken(authHeader);
            boolean isValid = isValid(token, headerEmail);
            logger.info("Token is {}", isValid);
            if(!isValid) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Auth failed!!");
                return false;
            }
            // Attach useful JWT payload to request for downstream usage
            try {
                Claims claims = jwtUtils.parseClaims(token);
                request.setAttribute("jwtClaims", claims);
                request.setAttribute("jwtSubject", claims.getSubject());
                request.setAttribute("authEmail", headerEmail);
            } catch (Exception e) {
                logger.warn("Failed to parse JWT claims: {}", e.getMessage());
                // Not fatal since token already validated, but proceed without attributes
            }
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        logger.info("API [{} {}] took {} ms", request.getMethod(), request.getRequestURI(), duration);
    }

    private boolean isValid(String token, String headerEmail) {
        boolean isTokenValid = jwtUtils.validateToken(token, headerEmail);
        logger.info("Token isValid: {}", isTokenValid);
        return isTokenValid;
    }

    private boolean shouldSkipAuth(HttpServletRequest request) {
        if(request.getRequestURL().toString().contains("users/signup") ||
                request.getRequestURL().toString().contains("users/signin")) {
            return true;
        }
        return false;
    }

    private String extractToken(String authHeader) {
        if (authHeader == null) return null;
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
