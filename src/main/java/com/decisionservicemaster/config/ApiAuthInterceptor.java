package com.decisionservicemaster.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiAuthInterceptor implements HandlerInterceptor {
    
    @Value("${api.token}")
    private String expectedToken;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getApiToken(request);
        
        if (token == null || !token.equals(expectedToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"api token mismatch\"}");
            return false;
        }
        
        return true;
    }
    
    private String getApiToken(HttpServletRequest request) {
        String token = request.getHeader("API-TOKEN");
        if (token == null) {
            token = request.getParameter("API_TOKEN");
        }
        return token;
    }
}