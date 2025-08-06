package com.failforward.deaddocs_backend.repository;

import com.failforward.deaddocs_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByStoryIdOrderByCreatedAtDesc(Integer storyId);
    List<Comment> findByUserId(String userId);
    long countByStoryId(Integer storyId);
    void deleteByStoryId(Integer storyId);
} 