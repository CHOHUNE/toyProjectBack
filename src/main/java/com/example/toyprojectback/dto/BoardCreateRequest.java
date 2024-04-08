package com.example.toyprojectback.dto;

import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.BoardCategory;
import com.example.toyprojectback.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;



//

@Schema(description = "Board 입력을 받아 DB에 저장할 때 사용하는 DTO")
@Data
public class BoardCreateRequest {
    private String title;
    private String body;
    private MultipartFile uploadImage;



    public Board toEntity(BoardCategory category, User user) {
        return Board.builder()
                .user(user)
                .category(category)
                .title(title)
                .body(body)
                .likeCnt(0)
                .commentCnt(0)
                .build();
    }

}
