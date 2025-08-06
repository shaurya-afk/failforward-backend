package com.failforward.deaddocs_backend;

import com.failforward.deaddocs_backend.dto.CommentRequest;
import com.failforward.deaddocs_backend.dto.CommentResponse;
import com.failforward.deaddocs_backend.entity.Comment;
import com.failforward.deaddocs_backend.entity.Story;
import com.failforward.deaddocs_backend.exceptions.StoryNotFoundException;
import com.failforward.deaddocs_backend.repository.CommentRepo;
import com.failforward.deaddocs_backend.service.CommentService;
import com.failforward.deaddocs_backend.service.StoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private StoryService storyService;

    @Test
    @Transactional
    public void testAddComment() throws StoryNotFoundException {
        // Create a test story first
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Test data
        String userId = "test-user-123";
        String commenterName = "Test User";
        String content = "This is a test comment";
        boolean isAnonymous = false;

        // Create comment request
        CommentRequest request = new CommentRequest();
        request.setStoryId(savedStory.getId());
        request.setUserId(userId);
        request.setCommenterName(commenterName);
        request.setAnonymous(isAnonymous);
        request.setContent(content);

        // Add comment
        CommentResponse response = commentService.addComment(request);

        // Verify response
        assertNotNull(response);
        assertEquals(savedStory.getId(), response.getStoryId());
        assertEquals(userId, response.getUserId());
        assertEquals(commenterName, response.getCommenterName());
        assertEquals(isAnonymous, response.isAnonymous());
        assertEquals(content, response.getContent());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    @Transactional
    public void testAddAnonymousComment() throws StoryNotFoundException {
        // Create a test story first
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Test data
        String userId = "test-user-456";
        String content = "This is an anonymous comment";
        boolean isAnonymous = true;

        // Create comment request
        CommentRequest request = new CommentRequest();
        request.setStoryId(savedStory.getId());
        request.setUserId(userId);
        request.setAnonymous(isAnonymous);
        request.setContent(content);

        // Add comment
        CommentResponse response = commentService.addComment(request);

        // Verify response
        assertNotNull(response);
        assertEquals(savedStory.getId(), response.getStoryId());
        assertEquals(userId, response.getUserId());
        assertEquals("Anonymous", response.getDisplayName());
        assertEquals(isAnonymous, response.isAnonymous());
        assertEquals(content, response.getContent());
    }

    @Test
    @Transactional
    public void testGetCommentsByStoryId() throws StoryNotFoundException {
        // Create a test story
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Add multiple comments
        addTestComment(savedStory.getId(), "user1", "Comment 1", false);
        addTestComment(savedStory.getId(), "user2", "Comment 2", true);
        addTestComment(savedStory.getId(), "user3", "Comment 3", false);

        // Get comments
        List<CommentResponse> comments = commentService.getCommentsByStoryId(savedStory.getId());

        // Verify comments
        assertEquals(3, comments.size());
        assertEquals("Comment 3", comments.get(0).getContent()); // Most recent first
        assertEquals("Comment 2", comments.get(1).getContent());
        assertEquals("Comment 1", comments.get(2).getContent());
    }

    @Test
    @Transactional
    public void testDeleteComment() throws StoryNotFoundException {
        // Create a test story
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Add a comment
        CommentResponse comment = addTestComment(savedStory.getId(), "test-user", "Test comment", false);

        // Verify comment exists
        List<CommentResponse> comments = commentService.getCommentsByStoryId(savedStory.getId());
        assertEquals(1, comments.size());

        // Delete comment
        boolean deleted = commentService.deleteComment(comment.getId(), "test-user");
        assertTrue(deleted);

        // Verify comment is deleted
        comments = commentService.getCommentsByStoryId(savedStory.getId());
        assertEquals(0, comments.size());
    }

    @Test
    @Transactional
    public void testDeleteCommentUnauthorized() throws StoryNotFoundException {
        // Create a test story
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Add a comment
        CommentResponse comment = addTestComment(savedStory.getId(), "user1", "Test comment", false);

        // Try to delete with different user
        boolean deleted = commentService.deleteComment(comment.getId(), "user2");
        assertFalse(deleted);

        // Verify comment still exists
        List<CommentResponse> comments = commentService.getCommentsByStoryId(savedStory.getId());
        assertEquals(1, comments.size());
    }

    @Test
    @Transactional
    public void testGetCommentCount() throws StoryNotFoundException {
        // Create a test story
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Initially no comments
        assertEquals(0, commentService.getCommentCount(savedStory.getId()));

        // Add comments
        addTestComment(savedStory.getId(), "user1", "Comment 1", false);
        addTestComment(savedStory.getId(), "user2", "Comment 2", true);

        // Verify count
        assertEquals(2, commentService.getCommentCount(savedStory.getId()));
    }

    @Test
    @Transactional
    public void testGetCommentsByUserId() throws StoryNotFoundException {
        // Create a test story
        Story testStory = createTestStory();
        Story savedStory = storyService.addStory(testStory);

        // Add comments by different users
        addTestComment(savedStory.getId(), "user1", "Comment by user1", false);
        addTestComment(savedStory.getId(), "user2", "Comment by user2", true);
        addTestComment(savedStory.getId(), "user1", "Another comment by user1", false);

        // Get comments by user1
        List<CommentResponse> user1Comments = commentService.getCommentsByUserId("user1");
        assertEquals(2, user1Comments.size());

        // Get comments by user2
        List<CommentResponse> user2Comments = commentService.getCommentsByUserId("user2");
        assertEquals(1, user2Comments.size());
    }

    @Test
    @Transactional
    public void testInvalidInputs() {
        // Test null story ID
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.getCommentsByStoryId(null);
        });

        // Test null user ID
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.getCommentsByUserId(null);
        });

        // Test null comment ID
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.deleteComment(null, "user");
        });
    }

    private Story createTestStory() {
        Story story = new Story();
        story.setUserId("test-founder");
        story.setFounderName("Test Founder");
        story.setAnonymous(false);
        story.setStoryTitle("Test Story");
        story.setIndustry("Technology");
        story.setPreviewText("This is a test story");
        story.setHelpfulVotes(0);
        story.setCommentCount(0);
        return story;
    }

    private CommentResponse addTestComment(Integer storyId, String userId, String content, boolean isAnonymous) throws StoryNotFoundException {
        CommentRequest request = new CommentRequest();
        request.setStoryId(storyId);
        request.setUserId(userId);
        request.setCommenterName("Test User");
        request.setAnonymous(isAnonymous);
        request.setContent(content);

        return commentService.addComment(request);
    }
} 