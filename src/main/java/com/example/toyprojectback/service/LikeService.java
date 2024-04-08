package com.example.toyprojectback.service;


import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.Like;
import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.repository.BoardRepository;
import com.example.toyprojectback.repository.LikeRepository;
import com.example.toyprojectback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void addLike(String loginId, Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        User loginUser =userRepository.findByLoginId(loginId).get();
        User boardUser = board.getUser();

        if (!boardUser.equals(loginUser)) {
            boardUser.likeChange(boardUser.getReceivedLikeCnt() + 1);
        }
        board.likeChange(board.getLikeCnt()+1);

        likeRepository.save(Like.builder()
                .user(loginUser)
                .board(board)
                .build());
    }

    @Transactional
    public void deleteLike(String loginId, Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        User loginUser = userRepository.findByLoginId(loginId).get();
        User boardUser = board.getUser();

        if (!boardUser.equals(loginUser)) {
            boardUser.likeChange(boardUser.getReceivedLikeCnt() - 1);

        }
        board.likeChange(board.getLikeCnt() - 1);

        likeRepository.deleteByUserLoginIdAndBoardId(loginId, boardId);
    }

    public Boolean checkLike(String loginId, Long boardId) {
        return likeRepository.existsByUserLoginIdAndBoardId(loginId, boardId);

    }


}
