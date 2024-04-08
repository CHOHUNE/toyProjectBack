package com.example.toyprojectback.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 시 사용하는 DTO")
public class UserLoginRequest {
    private String loginId;
    private String password;
}
