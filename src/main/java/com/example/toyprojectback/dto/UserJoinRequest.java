package com.example.toyprojectback.dto;

import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.entity.UserRole;
import lombok.Data;


import java.time.LocalDateTime;

@Data
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
