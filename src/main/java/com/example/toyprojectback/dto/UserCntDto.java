package com.example.toyprojectback.dto;


import com.example.toyprojectback.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "회원 정보 수정 시 사용하는 DTO")
public class UserCntDto {

    private String loginId;
    private String nickname;
    private String nowPassword;
    private String newPassword;
    private String newPasswordCheck;

    public static UserDto of(User user) {
        return UserDto.builder().
                loginId(user.getLoginId())
                .nickname(user.getNickname())
                .build();
    }
}
