package com.example.toyprojectback.entity;


import com.example.toyprojectback.dto.BoardDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @Enumerated(EnumType.STRING)
    //Enumrated : Enum 타입을 매핑할 때 사용 -> EnumType.STRING : enum 이름을 DB에 저장
    private BoardCategory category;

    //설명 : 지연로딩으로 설정 -> 해당 정보가 실제로 필요할 때 까지 조회하지 않음
    //ManyToOne 에서는 기본 타입이 FetchTyp.EAGER (즉시로딩 : 연관된 엔티티를 즉시 조회)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Like> likes;
    private Integer likeCnt;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Comment> comments;
    private Integer commentCnt;

    @OneToOne(fetch = FetchType.LAZY)
    private UploadImage uploadImage;


    public void update(BoardDto dto) {
        this.title = dto.getTitle();
        this.body = dto.getBody();
    }

    public void likeChange(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }

    public void commentChange(Integer commentCnt) {
        this.commentCnt = commentCnt;
    }

    public void setUploadImage(UploadImage uploadImage) {
        this.uploadImage = uploadImage;
    }
}

