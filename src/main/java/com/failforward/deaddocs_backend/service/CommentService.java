package com.failforward.deaddocs_backend.service;

import com.failforward.deaddocs_backend.dto.CommentRequest;
import com.failforward.deaddocs_backend.dto.CommentResponse;
import com.failforward.deaddocs_backend.entity.Comment;
import com.failforward.deaddocs_backend.entity.Story;
import com.failforward.deaddocs_backend.exceptions.StoryNotFoundException;
import com.failforward.deaddocs_backend.repository.CommentRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {
    private final CommentRepo commentRepo;
    private final StoryService storyService;

    public CommentService(CommentRepo commentRepo, StoryService storyService) {
        this.commentRepo = commentRepo;
        this.storyService = storyService;
    }

    /**
     * Add a comment to a story
     * @param commentRequest the comment request
     * @return the created comment response
     * @throws StoryNotFoundException if story not found
     */
    public CommentResponse addComment(CommentRequest commentRequest) throws StoryNotFoundException {
        // Validate inputs
        if (commentRequest.getStoryId() == null || commentRequest.getUserId() == null || 
            commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Story ID, user ID, and content are required");
        }

        // Verify story exists
        Story story = storyService.getStoryById(commentRequest.getStoryId());

        // Create comment
        Comment comment = new Comment();
        comment.setStoryId(commentRequest.getStoryId());
        comment.setUserId(commentRequest.getUserId());
        comment.setContent(commentRequest.getContent().trim());
        comment.setAnonymous(commentRequest.isAnonymous());

        // Handle commenter name based on anonymous setting
        if (commentRequest.isAnonymous()) {
            comment.setCommenterName(null);
        } else {
            comment.setCommenterName(commentRequest.getCommenterName() != null ? 
                commentRequest.getCommenterName().trim() : "Anonymous");
        }

        Comment savedComment = commentRepo.save(comment);

        // Update story comment count
        updateStoryCommentCount(commentRequest.getStoryId());

        return convertToResponse(savedComment);
    }

    /**
     * Get all comments for a story
     * @param storyId the story ID
     * @return list of comment responses
     */
    public List<CommentResponse> getCommentsByStoryId(Integer storyId) {
        if (storyId == null) {
            throw new IllegalArgumentException("Story ID cannot be null");
        }

        List<Comment> comments = commentRepo.findByStoryIdOrderByCreatedAtDesc(storyId);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get comments by user ID
     * @param userId the user ID
     * @return list of comment responses
     */
    public List<CommentResponse> getCommentsByUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<Comment> comments = commentRepo.findByUserId(userId);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete a comment
     * @param commentId the comment ID
     * @param userId the user ID (for authorization)
     * @return true if deleted, false if not found or unauthorized
     */
    public boolean deleteComment(Long commentId, String userId) {
        if (commentId == null || userId == null) {
            throw new IllegalArgumentException("Comment ID and user ID cannot be null");
        }

        return commentRepo.findById(commentId)
                .map(comment -> {
                    // Check if user owns the comment
                    if (!comment.getUserId().equals(userId)) {
                        return false;
                    }

                    Integer storyId = comment.getStoryId();
                    commentRepo.delete(comment);
                    
                    // Update story comment count
                    updateStoryCommentCount(storyId);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Get comment count for a story
     * @param storyId the story ID
     * @return the comment count
     */
    public long getCommentCount(Integer storyId) {
        if (storyId == null) {
            return 0;
        }
        return commentRepo.countByStoryId(storyId);
    }

    /**
     * Update story comment count
     * @param storyId the story ID
     */
    private void updateStoryCommentCount(Integer storyId) {
        try {
            long commentCount = getCommentCount(storyId);
            Story story = storyService.getStoryById(storyId);
            story.setCommentCount((int) commentCount);
            storyService.addStory(story);
        } catch (Exception e) {
            // Log error but don't fail the comment operation
            System.err.println("Error updating comment count for story " + storyId + ": " + e.getMessage());
        }
    }

    /**
     * Convert Comment entity to CommentResponse
     * @param comment the comment entity
     * @return the comment response
     */
    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setStoryId(comment.getStoryId());
        response.setUserId(comment.getUserId());
        response.setCommenterName(comment.getCommenterName());
        response.setAnonymous(comment.isAnonymous());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
} 