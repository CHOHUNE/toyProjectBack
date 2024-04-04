package com.example.toyprojectback.dto;


import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.Comment;
import com.example.toyprojectback.entity.User;
import lombok.Data;

@Data
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
