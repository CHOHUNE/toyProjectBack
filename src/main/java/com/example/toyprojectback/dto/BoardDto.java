package com.example.toyprojectback.dto;


import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.UploadImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "게시글 조회,리스트 조회, 수정 등에 사용되는 DTO")
public class BoardDto {

    private Long id;
    private String userLonginId;
    private String userNickname;
    private String title;
    private String body;
    private Integer likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private MultipartFile newImage;
    private UploadImage uploadImage;


    @Schema(description = " boardDto -> boardEntity")
    public static BoardDto of(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .userLonginId(board.getUser().getLoginId())
                .userNickname(board.getUser().getNickname())
                .title(board.getTitle())
                .body(board.getBody())
                .likeCnt(board.getLikeCnt())
                .createdAt(board.getCreatedAt())
                .lastModifiedAt(board.getLastModifiedAt())
                .uploadImage(board.getUploadImage())
                .build();

    }

}
