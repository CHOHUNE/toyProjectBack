package com.example.toyprojectback.dto;


import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.Comment;
import com.example.toyprojectback.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Comment 입력을 받아 DB에 저장할 때 사용하는 DTO")
public class CommentCreateRequest {
    private String body;
    public Comment toEntity(Board board, User user) {
        return Comment.builder()
                .user(user)
                .board(board)
                .body(body)
                .build();
    }
}
