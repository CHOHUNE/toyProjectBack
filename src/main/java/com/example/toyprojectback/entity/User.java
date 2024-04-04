package com.example.toyprojectback.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    private String nickname;

    private LocalDateTime createdAt;
    private Integer receivedLikeCnt;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments;

    public void rankUp(UserRole userRole, Authentication auth) {
        this.userRole = userRole;

        // 현재 저장되어 있는 Authentication 수정 => 재로그인 하지 않아도 권한이 바뀜
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority(userRole.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    //userRole 파라미터로 전달 받은 새로운 권한을 현재 사용자 엔티티에 저장
    // 기존 Authentication 객체를 가져옴
    // 새로운 권한 정보 (SimpleGrantedAuthortiy)를 추가한 ArrayList를 생성
    // 기존 Authentication 객체를 이용해 UsernamePasswordAuthenticationToken 객체를 생성
    // 여기에 새로운 권한 목록을 넘김
    // SecurityContextHolder에 새로운 Authentication 객체를 업데이트
    // 덧붙여 Authentication 객체에는 principal, credentials, authorities 가 있다.
    // principal : UserDetailsService에서 조회한 UserDetails


    public void likeChange(Integer receivedLikeCnt) {
        this.receivedLikeCnt = receivedLikeCnt;
        if (this.receivedLikeCnt >= 10 && this.userRole.equals(UserRole.SILVER)) {
            this.userRole = UserRole.GOLD;
        }
    }

    public void edit(String newPassword, String newNickname) {
        this.password = newPassword;
        this.nickname = newNickname;
    }

    public void changRole() {
        if (userRole.equals(UserRole.BRONZE)) userRole = UserRole.SILVER;
        else if (userRole.equals(UserRole.SILVER)) userRole = UserRole.GOLD;
        else if (userRole.equals(UserRole.GOLD)) userRole = UserRole.BLACKLIST;
        else if (userRole.equals(UserRole.BLACKLIST)) userRole = UserRole.BRONZE;

    }

}
