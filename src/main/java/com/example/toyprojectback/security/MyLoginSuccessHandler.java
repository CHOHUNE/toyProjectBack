package com.example.toyprojectback.security;


import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.entity.UserRole;
import com.example.toyprojectback.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

    //로그인 성공한 경우 MyLoginSuccessHandler 호출
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(); //세션 생성
        session.setMaxInactiveInterval(3600); //세션 유지 시간 1시간

        User loginuser = userRepository.findByLoginId(authentication.getName()).get(); //로그인한 유저 정보 가져오기

        //성공시 메세지 출력 후 홈화면으로 redirect
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter pw = response.getWriter();
        if(loginuser.getUserRole().equals(UserRole.BLACKLIST)){
            pw.println("<script>alert('"+loginuser.getLoginId()+"님은 블랙리스트 회원입니다.\\n로그인이 제한됩니다.'); location.href='/';</script>");
        }else{
            String prevPage = (String)request.getSession().getAttribute("prevPage");
            if (prevPage != null) {
                pw.println("<script>alert('"+loginuser.getNickname()+"님 환영합니다.'); location.href='"+prevPage+"';</script>");
            }else{
                pw.println("<script>alert('"+loginuser.getNickname()+"님 환영합니다.'); location.href='/';</script>");
            }
        }

        pw.flush(); //버퍼 비우기

    }

}
