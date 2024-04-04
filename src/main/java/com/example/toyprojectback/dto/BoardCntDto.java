package com.example.toyprojectback.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "홈 화면에서 각각의 카테고리에 해당하는 Board 수를 출력하기 위해 사용되는 DTO")
public class BoardCntDto {
    private Long totalNoticeCnt;
    private Long totalBoardCnt;
    private Long totalGreetingCnt;
    private Long totalFreeCnt;
    private Long totalGoldCnt;

}
