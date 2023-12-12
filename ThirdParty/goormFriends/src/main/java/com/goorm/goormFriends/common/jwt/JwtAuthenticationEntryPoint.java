package com.goorm.goormFriends.common.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException)
    throws IOException, ServletException {

        // 유효한 자격증명을 제공하지 않고 접근하려 할때
        response.sendRedirect(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
    }
}
