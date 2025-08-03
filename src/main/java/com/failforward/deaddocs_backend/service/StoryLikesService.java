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
        return storyLikeRepository.findByUserIdAndStoryId(userId, storyId).isPresent();
    }

    public void likeStory(String userId, Integer storyId) {
        if (!hasUserLiked(userId, storyId)) {
            StoryLike like = new StoryLike();
            like.setUserId(userId);
            like.setStoryId(storyId);
            storyLikeRepository.save(like);
        }
    }

    public void unlikeStory(String userId, Integer storyId) {
        storyLikeRepository.deleteByUserIdAndStoryId(userId, storyId);
    }

    public long getLikeCount(Integer storyId) {
        return storyLikeRepository.countByStoryId(storyId);
    }
}
