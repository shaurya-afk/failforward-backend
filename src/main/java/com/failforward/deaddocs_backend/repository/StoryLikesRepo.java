package com.failforward.deaddocs_backend.repository;

import com.failforward.deaddocs_backend.entity.StoryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryLikesRepo extends JpaRepository<StoryLike, Long> {
    Optional<StoryLike> findByUserIdAndStoryId(String userId, Integer storyId);
    long countByStoryId(Integer storyId);
    void deleteByUserIdAndStoryId(String userId, Integer storyId);
}
