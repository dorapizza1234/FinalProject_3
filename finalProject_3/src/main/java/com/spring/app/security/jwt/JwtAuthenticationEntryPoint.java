package com.spring.app.security.jwt;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String requestURI = request.getRequestURI();
        String ajaxHeader = request.getHeader("X-Requested-With");
        String acceptHeader = request.getHeader("Accept");
        String contentType = request.getContentType();

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(ajaxHeader);
        boolean isJsonRequest =
                (acceptHeader != null && acceptHeader.contains("application/json")) ||
                (contentType != null && contentType.contains("application/json")) ||
                (requestURI != null && requestURI.startsWith(request.getContextPath() + "/api/")) ||
                (requestURI != null && requestURI.startsWith("/api/"));

        if (isAjax || isJsonRequest) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        String contextPath = request.getContextPath();
        String queryString = request.getQueryString();

        String fullRequestUri = requestURI;
        if (queryString != null && !queryString.trim().isEmpty()) {
            fullRequestUri += "?" + queryString;
        }

        String redirectParam = URLEncoder.encode(fullRequestUri, StandardCharsets.UTF_8);
        response.sendRedirect(contextPath + "/security/login?redirect=" + redirectParam);
    }
}