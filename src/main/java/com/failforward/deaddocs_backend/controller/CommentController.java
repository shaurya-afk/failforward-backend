package com.failforward.deaddocs_backend.controller;

import com.failforward.deaddocs_backend.dto.CommentRequest;
import com.failforward.deaddocs_backend.dto.CommentResponse;
import com.failforward.deaddocs_backend.exceptions.StoryNotFoundException;
import com.failforward.deaddocs_backend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a story
     */
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {
        try {
            // Validate inputs
            if (commentRequest.getStoryId() == null || commentRequest.getUserId() == null || 
                commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Story ID, user ID, and content are required");
            }

            CommentResponse comment = commentService.addComment(commentRequest);
            return ResponseEntity.ok(comment);
        } catch (StoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding comment: " + e.getMessage());
        }
    }

    /**
     * Get all comments for a story
     */
    @GetMapping("/story/{storyId}")
    public ResponseEntity<?> getCommentsByStoryId(@PathVariable Integer storyId) {
        try {
            if (storyId == null) {
                return ResponseEntity.badRequest().body("Story ID cannot be null");
            }

            List<CommentResponse> comments = commentService.getCommentsByStoryId(storyId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching comments: " + e.getMessage());
        }
    }

    /**
     * Get comments by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCommentsByUserId(@PathVariable String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID cannot be null or empty");
            }

            List<CommentResponse> comments = commentService.getCommentsByUserId(userId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching user comments: " + e.getMessage());
        }
    }

    /**
     * Delete a comment
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestParam String userId) {
        try {
            if (commentId == null || userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Comment ID and user ID are required");
            }

            boolean deleted = commentService.deleteComment(commentId, userId);
            if (deleted) {
                return ResponseEntity.ok("Comment deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting comment: " + e.getMessage());
        }
    }

    /**
     * Get comment count for a story
     */
    @GetMapping("/count/{storyId}")
    public ResponseEntity<?> getCommentCount(@PathVariable Integer storyId) {
        try {
            if (storyId == null) {
                return ResponseEntity.badRequest().body("Story ID cannot be null");
            }

            long count = commentService.getCommentCount(storyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error getting comment count: " + e.getMessage());
        }
    }
} 