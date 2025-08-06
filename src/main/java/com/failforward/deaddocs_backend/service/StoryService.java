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
     * Sync the helpful votes count with the actual like count from the database
     * This ensures consistency between the Story entity and the story_likes table
     * @param storyId the story ID to sync
     * @return the updated story with correct like count
     * @throws StoryNotFoundException if story not found
     */
    public Story syncLikeCount(Integer storyId) throws StoryNotFoundException {
        Story story = getStoryById(storyId);
        int accurateLikeCount = storyLikesService.getAccurateLikeCount(storyId);
        story.setHelpfulVotes(accurateLikeCount);
        return storyRepo.save(story);
    }

    /**
     * Sync all stories' like counts to ensure database consistency
     * This method should be called periodically or when inconsistencies are detected
     */
    public void syncAllLikeCounts() {
        List<Story> allStories = getAllStories();
        for (Story story : allStories) {
            try {
                syncLikeCount(story.getId());
            } catch (Exception e) {
                // Log error but continue with other stories
                System.err.println("Error syncing like count for story " + story.getId() + ": " + e.getMessage());
            }
        }
    }
}
