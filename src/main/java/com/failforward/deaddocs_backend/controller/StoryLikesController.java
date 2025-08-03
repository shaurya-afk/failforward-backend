package com.failforward.deaddocs_backend.controller;

import com.failforward.deaddocs_backend.service.StoryLikesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stories")
public class StoryLikesController {
    private final StoryLikesService storyLikesService;

    public StoryLikesController(StoryLikesService storyLikesService) {
        this.storyLikesService = storyLikesService;
    }

    @PostMapping("/{storyId}/like")
    public ResponseEntity<?> likeStory(@PathVariable("storyId") Integer storyId,
                                       @RequestParam("userId") String userId) {
        storyLikesService.likeStory(userId, storyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{storyId}/unlike")
    public ResponseEntity<?> unlikeStory(@PathVariable Integer storyId, @RequestParam String userId) {
        storyLikesService.unlikeStory(userId, storyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storyId}/liked")
    public ResponseEntity<Boolean> hasUserLiked(@PathVariable Integer storyId, @RequestParam String userId) {
        boolean liked = storyLikesService.hasUserLiked(userId, storyId);
        return ResponseEntity.ok(liked);
    }

//    @GetMapping("/{}/count_likes")
}
