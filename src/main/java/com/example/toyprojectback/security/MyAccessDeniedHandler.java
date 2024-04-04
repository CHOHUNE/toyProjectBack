package com.example.toyprojectback.security;

import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.entity.UserRole;
import com.example.toyprojectback.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

//인가에 실패한 경우 ( 일반 유저가 관리자 권한이 필요한 URL에 접근하려고 할 때 ) 403 에러를 리턴하는 핸들러

@RequiredArgsConstructor
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    private final UserRepository userRepository;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = null;
        if(auth != null) { //로그인한 유저가 있을 때
            loginUser = userRepository.findByLoginId(auth.getName()).get();
            //로그인한 유저 정보 가져오기 (로그인 아이디로)

        }

        String requestURI = request.getRequestURI();

        //로그인한 유저가 login,join을 시도하는 경우

        if(requestURI.contains("/users/login") || requestURI.contains("/users/join")) {
            //메세지 출력 후 홈으로 redirect
            response.setContentType("text/html"); //응답할 데이터의 종류
            PrintWriter pw = response.getWriter();//응답할 데이터를 작성할 수 있는 객체
            pw.println("<script>alert('이미 로그인 되어있습니다.'); location.href='/';</script>"); //응답할 데이터 작성
            pw.flush(); //응답

        } else if (requestURI.contains("gold")) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('골드 등급 이상의 유저만 가능 합니다!'); location.href='/';</script>");
            pw.flush();


        }else if(loginUser != null && loginUser.getUserRole().equals(UserRole.BLACKLIST)) {
            //블랙리스트인 경우 경고메세지와 redriect
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('블랙리스트는 글,댓글 작성이 불가 합니다.'); location.href='/';</script>");
            pw.flush();
        } else if(requestURI.contains("free/write")){
            // 브론즈 등급이 자유 게시판에 글을 올리려는 경우
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('가입인사 작성 후 작성 가능 합니다.'); location.href='/boards/greeting';</script>");
            pw.flush();

        } else if (requestURI.contains("greeting")) {
            //실버 등급 이상이 가입 인사를 하려 할 경우
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('실버 등급 이상의 유저만 가능 합니다!'); location.href='/boards/greeting';</script>");
            pw.flush();
        } else if (requestURI.contains("admin")) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('관리자만 가능합니다.'); location.href='/';</script>");
            pw.flush();
        }


    }
}
