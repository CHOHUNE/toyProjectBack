package com.example.toyprojectback.repository;

import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.BoardCategory;
import com.example.toyprojectback.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findAllByCategoryAndUserUserRoleNot(BoardCategory category, UserRole user_userRole,PageRequest pageRequest);
    //Board 엔티티에서 category 필드와 user.userRole 필드를 이용한 조회
    // user.userRole 이 주어진 role 과 다른 것만 조회 (not)
    // PageRequest 를 이용하여 페이징 처리

    Page<Board> findAllByCategoryAndTitleContainsAndUserUserRoleNot(BoardCategory category, String title, UserRole userRole, PageRequest pageRequest);
    //Board category, title 필드 및 user.userRole 필드 조회
    // title 이 주어진 text를 포함하는지 체크 (Contains)
    // PageRequest 를 이용하여 페이징 처리

    Page<Board> findAllByCategoryAndUserNicknameContainsAndUserUserRoleNot(BoardCategory category, String nickname, UserRole userRole, PageRequest pageRequest);
    //Board category, user.nickname 필드 및 user.userRole 필드 조회
    // user.nickname 이 주어진 text를 포함하는지 체크 (Contains)
    // PageRequest 를 이용하여 페이징 처리

    List<Board> findAllByUserLoginId(String loginId);
    //user.loginId 로 Board 조회


    List<Board> findAllByCategoryAndUserUserRole(BoardCategory category, UserRole userRole);
    //Category, User.userRole 필드로 Board 엔티티 리스트로 조회

    Long countAllByUserUserRole(UserRole userRole);
    // 전체 공지글이 몇개 있는지 조회 시 사용

    Long countAllByCategoryAndUserUserRoleNot(BoardCategory category, UserRole userRole);
    // 해당 카테고리 공지글을 제외한 글이 몇개 있는지 조회 시 사용



}
