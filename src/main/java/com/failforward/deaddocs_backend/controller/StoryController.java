package com.failforward.deaddocs_backend.controller;

import com.failforward.deaddocs_backend.dto.StoryRequest;
import com.failforward.deaddocs_backend.dto.StoryResponse;
import com.failforward.deaddocs_backend.entity.Story;
import com.failforward.deaddocs_backend.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public ResponseEntity<List<StoryResponse>> getAllStories(){
        List<Story> stories = storyService.getAllStories();
        List<StoryResponse> responses = stories.stream().map(story -> {
            StoryResponse response = new StoryResponse();
            response.setId(story.getId());
            response.setUserId(story.getUserId());
            response.setFounderName(story.getFounderName());
            response.setAnonymous(story.isAnonymous());
            response.setStoryTitle(story.getStoryTitle());
            response.setIndustry(story.getIndustry());
            response.setPreviewText(story.getPreviewText());
            response.setHelpfulVotes(story.getHelpfulVotes());
            response.setCommentCount(story.getCommentCount());
            return response;
        }).toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<StoryResponse> addStory(@RequestBody StoryRequest request) {
        Story newStory = new Story();
        newStory.setUserId(request.getUserId());
        newStory.setFounderName(request.getFounderName());
        newStory.setAnonymous(request.isAnonymous());
        newStory.setStoryTitle(request.getStoryTitle());
        newStory.setIndustry(request.getIndustry());
        newStory.setPreviewText(request.getPreviewText());
        newStory.setHelpfulVotes(request.getHelpfulVotes());
        newStory.setCommentCount(request.getCommentCount());

        Story savedStory;
        try {
            savedStory = storyService.addStory(newStory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        StoryResponse response = new StoryResponse();
        response.setId(savedStory.getId());
        response.setUserId(savedStory.getUserId());
        response.setFounderName(savedStory.getFounderName());
        response.setAnonymous(savedStory.isAnonymous());
        response.setStoryTitle(savedStory.getStoryTitle());
        response.setIndustry(savedStory.getIndustry());
        response.setPreviewText(savedStory.getPreviewText());
        response.setHelpfulVotes(savedStory.getHelpfulVotes());
        response.setCommentCount(savedStory.getCommentCount());

        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable("id") Integer id){
        storyService.deleteStoryById(id);
        return ResponseEntity.status(204).build();
    }
}
