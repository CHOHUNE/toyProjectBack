package com.example.toyprojectback.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //역할 : JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 컬럼으로 인식하도록 한다.
@EntityListeners(AuditingEntityListener.class) // 역할 : JPA Entity에서 이벤트가 발생할 때마다 콜백을 받는다.
public class BaseEntity {


    @CreatedDate // 생성일자를 관리하는 필드에 현재 날짜를 주입하는 작업 수행
    @Column(updatable = false) //생성일자, 생성자에 대한 필드이기 때문에 수정 불가하도록 설정
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime   lastModified;

}
