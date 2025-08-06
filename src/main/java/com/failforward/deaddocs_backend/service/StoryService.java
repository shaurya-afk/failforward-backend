package com.failforward.deaddocs_backend.service;

import com.failforward.deaddocs_backend.entity.Story;
import com.failforward.deaddocs_backend.exceptions.StoryNotFoundException;
import com.failforward.deaddocs_backend.repository.StoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StoryService {
    private final StoryRepo storyRepo;
    private final StoryLikesService storyLikesService;

    public StoryService(StoryRepo storyRepo, StoryLikesService storyLikesService) {
        this.storyRepo = storyRepo;
        this.storyLikesService = storyLikesService;
    }

    public List<Story> getAllStories(){
        return storyRepo.findAll();
    }

    public Story getStoryById(int id) throws StoryNotFoundException {
        return storyRepo.findById(id).orElseThrow(() -> new StoryNotFoundException("Story not found with id: "+id));
    }

    public Story getStoryByUserId(String userId){
        return storyRepo.getStoriesByUserId(userId);
    }

    public List<Story> getAllStoriesByUserId(String userId){
        return storyRepo.findAllByUserId(userId);
    }

    public void deleteStoryById(Integer id){
        storyRepo.deleteStoryById(id);
    }

    public Story addStory(Story story){
        return storyRepo.save(story);
    }

    public int getHelpfulVotesById(int id) throws StoryNotFoundException {
        Story story = getStoryById(id);
        if(story != null){
            return  story.getHelpfulVotes();
        }else {
            throw new NullPointerException("Story not found");
        }
    }
    
    public Story updateStoryByHelpfulVotes(Integer id, int newHelpfulVotes) throws StoryNotFoundException {
        Story story = getStoryById(id);
        story.setHelpfulVotes(newHelpfulVotes);
        return storyRepo.save(story);
    }

    /**
     * Sync like count for a specific story
     * This ensures consistency between Story entity and story_likes table
     */
    public Story syncLikeCount(Integer storyId) throws StoryNotFoundException {
        Story story = getStoryById(storyId);
        int accurateCount = (int) storyLikesService.getLikeCount(storyId);
        story.setHelpfulVotes(accurateCount);
        return storyRepo.save(story);
    }

    /**
     * Sync like counts for all stories
     * This ensures database consistency across all stories
     */
    public void syncAllLikeCounts() {
        List<Story> allStories = getAllStories();
        for (Story story : allStories) {
            try {
                syncLikeCount(story.getId());
            } catch (StoryNotFoundException e) {
                // Log error but continue with other stories
                System.err.println("Story not found during sync: " + story.getId());
            }
        }
    }
}
