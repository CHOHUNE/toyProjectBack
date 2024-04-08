package com.example.toyprojectback.repository;

import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);
    Page<User> findAllByNicknameContains(String nickname, PageRequest pageRequest);
    // nickname으로 검색 조건을 지정하여 페이징 처리된 User 엔티티 결과를 반환
    Boolean existsByLoginId(String nickname);
    Long countAllByUserRole(UserRole userRole);
    boolean existsByNickname(String nickname);
}

// Page : 페이징된 데이터, 페이징 정보를 담는 객체
// content :페이징된 데이터 목록
// totalElements :전체 데이터 수
// totalPages : 전체 페이지 수
// number : 현재 페이지 번호

// PageRequest : 페이징 처리를 위한 정보를 담는 객체
// page : 페이지 번호 ,size : 페이지당 데이터 수 ,sort : 정렬 정보

// 요약 : Repository 조회 메서드에서 PageRequest 를 파라미터로 받아 페이징 조건을 지정
// Page 타입으로 반환하여 페이징된 결고 ㅏ처리

// Page: 페이징 결과 데이터 자체
// PageReqeust : 페이징 조건 설정
