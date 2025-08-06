package com.failforward.deaddocs_backend.controller;

import com.failforward.deaddocs_backend.entity.Story;
import com.failforward.deaddocs_backend.exceptions.StoryNotFoundException;
import com.failforward.deaddocs_backend.service.StoryLikesService;
import com.failforward.deaddocs_backend.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stories")
public class StoryLikesController {
    private final StoryLikesService storyLikesService;
    private final StoryService storyService;

    public StoryLikesController(StoryLikesService storyLikesService, StoryService storyService) {
        this.storyLikesService = storyLikesService;
        this.storyService = storyService;
    }

    @PostMapping("/{storyId}/like")
    public ResponseEntity<?> likeStory(@PathVariable("storyId") Integer storyId,
                                       @RequestParam("userId") String userId) throws StoryNotFoundException {
        try {
            // Validate inputs
            if (storyId == null || userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid storyId or userId");
            }

            // Get current story
            Story currentStory = storyService.getStoryById(storyId);
            
            // Like the story and get new count
            int newLikeCount = storyLikesService.likeStoryAndGetNewCount(userId, storyId, currentStory.getHelpfulVotes());
            
            // Update the story's helpful votes count
            Story updatedStory = storyService.updateStoryByHelpfulVotes(storyId, newLikeCount);
            
            return ResponseEntity.ok(updatedStory);
        } catch (StoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing like request: " + e.getMessage());
        }
    }

    @PostMapping("/{storyId}/unlike")
    public ResponseEntity<?> unlikeStory(@PathVariable Integer storyId, @RequestParam String userId) throws StoryNotFoundException {
        try {
            // Validate inputs
            if (storyId == null || userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid storyId or userId");
            }

            // Get current story
            Story currentStory = storyService.getStoryById(storyId);
            
            // Unlike the story and get new count
            int newLikeCount = storyLikesService.unlikeStoryAndGetNewCount(userId, storyId, currentStory.getHelpfulVotes());
            
            // Update the story's helpful votes count
            Story updatedStory = storyService.updateStoryByHelpfulVotes(storyId, newLikeCount);
            
            return ResponseEntity.ok(updatedStory);
        } catch (StoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing unlike request: " + e.getMessage());
        }
    }

    @GetMapping("/{storyId}/liked")
    public ResponseEntity<Boolean> hasUserLiked(@PathVariable Integer storyId, @RequestParam String userId) {
        try {
            // Validate inputs
            if (storyId == null || userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            boolean liked = storyLikesService.hasUserLiked(userId, storyId);
            return ResponseEntity.ok(liked);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{storyId}/like-count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Integer storyId) {
        try {
            if (storyId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            long likeCount = storyLikesService.getLikeCount(storyId);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get accurate like count for a story
     * This endpoint ensures consistency by counting actual likes from the database
     */
    @GetMapping("/{storyId}/accurate-like-count")
    public ResponseEntity<Integer> getAccurateLikeCount(@PathVariable Integer storyId) {
        try {
            if (storyId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            int accurateCount = storyLikesService.getAccurateLikeCount(storyId);
            return ResponseEntity.ok(accurateCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Sync like count for a specific story
     * This ensures consistency between Story entity and story_likes table
     */
    @PostMapping("/{storyId}/sync-like-count")
    public ResponseEntity<?> syncLikeCount(@PathVariable Integer storyId) {
        try {
            if (storyId == null) {
                return ResponseEntity.badRequest().body("Invalid storyId");
            }
            
            Story updatedStory = storyService.syncLikeCount(storyId);
            return ResponseEntity.ok(updatedStory);
        } catch (StoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error syncing like count: " + e.getMessage());
        }
    }

    /**
     * Sync like counts for all stories
     * This ensures database consistency across all stories
     */
    @PostMapping("/sync-all-like-counts")
    public ResponseEntity<?> syncAllLikeCounts() {
        try {
            storyService.syncAllLikeCounts();
            return ResponseEntity.ok().body("Successfully synced like counts for all stories");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error syncing all like counts: " + e.getMessage());
        }
    }
}
