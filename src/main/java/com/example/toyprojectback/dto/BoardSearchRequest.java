package com.example.toyprojectback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "게시글 리스트에서 검색할 때 사용하는 DTO")
public class BoardSearchRequest {
    private String sortType;
    private String searchType;
    private String keyword;

}
