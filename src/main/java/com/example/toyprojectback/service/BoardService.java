package com.example.toyprojectback.service;

import com.example.toyprojectback.dto.BoardCntDto;
import com.example.toyprojectback.dto.BoardCreateRequest;
import com.example.toyprojectback.dto.BoardDto;
import com.example.toyprojectback.entity.*;
import com.example.toyprojectback.repository.BoardRepository;
import com.example.toyprojectback.repository.CommentRepository;
import com.example.toyprojectback.repository.LikeRepository;
import com.example.toyprojectback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UploadImageService uploadImageService;

    public Page<Board> getBoardList(BoardCategory category, PageRequest pageRequest, String searchType, String keyword) {
        if (searchType != null && keyword != null) {
            if (searchType.equals("title")) {
                return boardRepository.findAllByCategoryAndTitleContainsAndUserUserRoleNot(category, keyword, UserRole.ADMIN, pageRequest);
            } else {
                return boardRepository.findAllByCategoryAndUserNicknameContainsAndUserUserRoleNot(category, keyword, UserRole.ADMIN, pageRequest);

            }
        }

        return boardRepository.findAllByCategoryAndUserUserRoleNot(category, UserRole.ADMIN, pageRequest);

    }

    public List<Board> getNotice(BoardCategory category) {
        return boardRepository.findAllByCategoryAndUserUserRole(category, UserRole.ADMIN);
    }
    //공지만 가져오기

    public BoardDto getBoard(Long boardId, String category) {
        Optional<Board> optBoard = boardRepository.findById(boardId);
        //Optional로 반환하는 의도
        // null 체크 간소화, nullPointerException 방지, 의도 표현 값이 없을 수 있다.

        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null;
            // 보드가 빈 값이거나, 카테고리가 대소문자 관계 없이 맞지 않으면 null 반환
        }

        return BoardDto.of(optBoard.get()); // of : BoardDto 객체 생성
    }

    @Transactional
    public Long writeBoard(BoardCreateRequest req, BoardCategory category, String loginId, Authentication auth) throws IOException {

        User loginUser = userRepository.findByLoginId(loginId).get();

        Board saveBoard = boardRepository.save(req.toEntity(category, loginUser));

        UploadImage uploadImage = uploadImageService.saveImage(req.getUploadImage(), saveBoard);
        if (uploadImage != null) {
            saveBoard.setUploadImage(uploadImage);
        }
        if (category.equals(BoardCategory.GREETING)) {
            loginUser.rankUp(UserRole.SILVER, auth);
        }
        return saveBoard.getId();

    }

    @Transactional
    public Long editBoard(Long boardId, String category, BoardDto dto) throws IOException {

        Optional<Board> optBoard = boardRepository.findById(boardId);

        // id 에 해당하는 게시글이 없거나 카테고리가 일치 하지 않으면 null return

        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null;
        }

        Board board = optBoard.get();

        if (board.getUploadImage() != null) {
            uploadImageService.deleteImage(board.getUploadImage());
            board.setUploadImage(null);

        }

        UploadImage uploadImage = uploadImageService.saveImage(dto.getNewImage(), board);
        if (uploadImage != null) {
            board.setUploadImage(uploadImage);
        }
        board.update(dto);

        return board.getId();
    }

    public Long deleteBoard(Long boardId, String category) throws IOException {

        Optional<Board> optBoard = boardRepository.findById(boardId);

        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null; //.get()메서드는 Optional 로 감싸져 있는 객체를 가져오는 메서드이다.
        }
        User boardUser = optBoard.get().getUser();
        return boardId;

    }

    public String getCategory(Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        return board.getCategory().toString().toLowerCase();
    }

    public List<Board> findMyBoard(String category, String loginId) {

        if (category.equals("board")) {
            return boardRepository.findAllByUserLoginId(loginId);

        } else if (category.equals("like")) {
            List<Like> likes = likeRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            for (Like like : likes) {
                boards.add(like.getBoard());
            }

            return boards;
        } else if (category.equals("comment")) {
            List<Comment> comments = commentRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            HashSet<Long> commentIds = new HashSet<>();

            for (Comment comment : comments) {

                if (!commentIds.contains(comment.getBoard().getId())) {
                    boards.add(comment.getBoard());
                    commentIds.add(comment.getBoard().getId());
                }
            }

            return boards;
        }
        return null;
    }

    public BoardCntDto getBoardCnt() {
        return BoardCntDto.builder()
                .totalBoardCnt(boardRepository.count())
                .totalNoticeCnt(boardRepository.countAllByUserUserRole(UserRole.ADMIN))
                .totalGreetingCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GREETING, UserRole.ADMIN))
                .totalFreeCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.FREE, UserRole.ADMIN))
                .totalGoldCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GOLD, UserRole.ADMIN))
                .build();
    }
}


