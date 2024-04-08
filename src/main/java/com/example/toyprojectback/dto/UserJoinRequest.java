package com.example.toyprojectback.dto;

import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Schema(description = "회원가입 시 사용하는 DTO")
public class UserJoinRequest {
    private String loginId;
    private String password;
    private String passwordCheck;
    private String nickname;

    public User toEntity(String encodedPassword) {

        return  User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .createdAt(LocalDateTime.now())
                .receivedLikeCnt(0)
                .userRole(UserRole.BRONZE)
                .build();
    }
}
