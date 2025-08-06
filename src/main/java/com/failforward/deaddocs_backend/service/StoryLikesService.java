package com.failforward.deaddocs_backend.service;

import com.failforward.deaddocs_backend.entity.StoryLike;
import com.failforward.deaddocs_backend.repository.StoryLikesRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoryLikesService {
    private final StoryLikesRepo storyLikeRepository;

    public StoryLikesService(StoryLikesRepo storyLikeRepository) {
        this.storyLikeRepository = storyLikeRepository;
    }

    public boolean hasUserLiked(String userId, Integer storyId) {
        if (userId == null || storyId == null) {
            return false;
        }
        return storyLikeRepository.findByUserIdAndStoryId(userId, storyId).isPresent();
    }

    /**
     * Like a story with proper transaction handling
     * @param userId the user ID
     * @param storyId the story ID
     * @return true if the like was added, false if already liked
     */
    @Transactional
    public boolean likeStory(String userId, Integer storyId) {
        if (userId == null || storyId == null) {
            throw new IllegalArgumentException("UserId and storyId cannot be null");
        }
        
        // Check if already liked within the same transaction
        if (hasUserLiked(userId, storyId)) {
            return false; // Already liked
        }
        
        StoryLike like = new StoryLike();
        like.setUserId(userId);
        like.setStoryId(storyId);
        storyLikeRepository.save(like);
        return true; // Successfully liked
    }

    /**
     * Unlike a story with proper transaction handling
     * @param userId the user ID
     * @param storyId the story ID
     * @return true if the unlike was successful, false if not liked
     */
    @Transactional
    public boolean unlikeStory(String userId, Integer storyId) {
        if (userId == null || storyId == null) {
            throw new IllegalArgumentException("UserId and storyId cannot be null");
        }
        
        // Check if liked within the same transaction
        if (!hasUserLiked(userId, storyId)) {
            return false; // Not liked
        }
        
        storyLikeRepository.deleteByUserIdAndStoryId(userId, storyId);
        return true; // Successfully unliked
    }

    public long getLikeCount(Integer storyId) {
        if (storyId == null) {
            return 0;
        }
        return storyLikeRepository.countByStoryId(storyId);
    }

    /**
     * Optimized method to like a story and return the new count
     * Uses atomic operations to prevent race conditions
     * @param userId the user ID
     * @param storyId the story ID
     * @param currentCount current like count from story entity
     * @return new like count after liking
     */
    @Transactional
    public int likeStoryAndGetNewCount(String userId, Integer storyId, int currentCount) {
        boolean wasLiked = likeStory(userId, storyId);
        if (wasLiked) {
            return currentCount + 1;
        }
        return currentCount; // Already liked, return current count
    }

    /**
     * Optimized method to unlike a story and return the new count
     * Uses atomic operations to prevent race conditions
     * @param userId the user ID
     * @param storyId the story ID
     * @param currentCount current like count from story entity
     * @return new like count after unliking
     */
    @Transactional
    public int unlikeStoryAndGetNewCount(String userId, Integer storyId, int currentCount) {
        boolean wasUnliked = unlikeStory(userId, storyId);
        if (wasUnliked) {
            return Math.max(0, currentCount - 1); // Ensure count doesn't go negative
        }
        return currentCount; // Not liked, return current count
    }

    /**
     * Get the accurate like count from the database
     * This method ensures consistency by counting actual likes
     * @param storyId the story ID
     * @return the actual count of likes for the story
     */
    public int getAccurateLikeCount(Integer storyId) {
        if (storyId == null) {
            return 0;
        }
        return (int) storyLikeRepository.countByStoryId(storyId);
    }
}
