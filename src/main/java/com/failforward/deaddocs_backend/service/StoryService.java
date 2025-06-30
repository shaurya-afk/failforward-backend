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

    public StoryService(StoryRepo storyRepo) {
        this.storyRepo = storyRepo;
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
    public void updateStoryByHelpfulVotes(Integer id, int newHelpfulVotes) throws StoryNotFoundException {
        Story story = getStoryById(id);
        if(story != null){
            story.setHelpfulVotes(newHelpfulVotes);
        }else{
            throw new NullPointerException("Story not found");
        }
    }
}
