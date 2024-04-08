package com.example.toyprojectback.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;

public class MyAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //메세지 출력 후 홈으로 redirect
        response.sendRedirect("text/html");
        PrintWriter pw = response.getWriter();
        pw.println("<script>alert('로그인이 필요합니다.'); location.href='/';</script>");
        pw.flush();

    }
}
