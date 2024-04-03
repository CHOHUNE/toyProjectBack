package com.example.toyprojectback.repository;


import com.example.toyprojectback.entity.Comment;
import com.example.toyprojectback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    public List<Comment> findAllByUserLoginId(String loginId);

        void deleteByUser(User loginUser);
}
