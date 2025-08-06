package com.failforward.deaddocs_backend;

import com.failforward.deaddocs_backend.entity.StoryLike;
import com.failforward.deaddocs_backend.repository.StoryLikesRepo;
import com.failforward.deaddocs_backend.service.StoryLikesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StoryLikesServiceTest {

    @Autowired
    private StoryLikesService storyLikesService;

    @Autowired
    private StoryLikesRepo storyLikesRepo;

    @Test
    @Transactional
    public void testLikeStory() {
        // Test data
        String userId = "test-user-123";
        Integer storyId = 1;

        // Test initial state
        assertFalse(storyLikesService.hasUserLiked(userId, storyId));
        assertEquals(0, storyLikesService.getLikeCount(storyId));

        // Test liking a story
        boolean liked = storyLikesService.likeStory(userId, storyId);
        assertTrue(liked);
        assertTrue(storyLikesService.hasUserLiked(userId, storyId));
        assertEquals(1, storyLikesService.getLikeCount(storyId));

        // Test liking the same story again (should return false)
        boolean likedAgain = storyLikesService.likeStory(userId, storyId);
        assertFalse(likedAgain);
        assertEquals(1, storyLikesService.getLikeCount(storyId));
    }

    @Test
    @Transactional
    public void testUnlikeStory() {
        // Test data
        String userId = "test-user-456";
        Integer storyId = 2;

        // First like the story
        storyLikesService.likeStory(userId, storyId);
        assertTrue(storyLikesService.hasUserLiked(userId, storyId));
        assertEquals(1, storyLikesService.getLikeCount(storyId));

        // Test unliking the story
        boolean unliked = storyLikesService.unlikeStory(userId, storyId);
        assertTrue(unliked);
        assertFalse(storyLikesService.hasUserLiked(userId, storyId));
        assertEquals(0, storyLikesService.getLikeCount(storyId));

        // Test unliking again (should return false)
        boolean unlikedAgain = storyLikesService.unlikeStory(userId, storyId);
        assertFalse(unlikedAgain);
        assertEquals(0, storyLikesService.getLikeCount(storyId));
    }

    @Test
    @Transactional
    public void testLikeStoryAndGetNewCount() {
        // Test data
        String userId = "test-user-789";
        Integer storyId = 3;
        int currentCount = 5;

        // Test liking and getting new count
        int newCount = storyLikesService.likeStoryAndGetNewCount(userId, storyId, currentCount);
        assertEquals(6, newCount);
        assertEquals(1, storyLikesService.getLikeCount(storyId));

        // Test liking again (should return same count)
        int newCount2 = storyLikesService.likeStoryAndGetNewCount(userId, storyId, newCount);
        assertEquals(6, newCount2); // Should not increase
    }

    @Test
    @Transactional
    public void testUnlikeStoryAndGetNewCount() {
        // Test data
        String userId = "test-user-101";
        Integer storyId = 4;
        int currentCount = 3;

        // First like the story
        storyLikesService.likeStory(userId, storyId);

        // Test unliking and getting new count
        int newCount = storyLikesService.unlikeStoryAndGetNewCount(userId, storyId, currentCount);
        assertEquals(2, newCount);
        assertEquals(0, storyLikesService.getLikeCount(storyId));

        // Test unliking again (should return same count)
        int newCount2 = storyLikesService.unlikeStoryAndGetNewCount(userId, storyId, newCount);
        assertEquals(2, newCount2); // Should not decrease further
    }

    @Test
    @Transactional
    public void testMultipleUsersLikingSameStory() {
        // Test data
        String userId1 = "user-1";
        String userId2 = "user-2";
        Integer storyId = 5;

        // Both users like the story
        storyLikesService.likeStory(userId1, storyId);
        storyLikesService.likeStory(userId2, storyId);

        // Verify both users have liked
        assertTrue(storyLikesService.hasUserLiked(userId1, storyId));
        assertTrue(storyLikesService.hasUserLiked(userId2, storyId));
        assertEquals(2, storyLikesService.getLikeCount(storyId));

        // One user unlikes
        storyLikesService.unlikeStory(userId1, storyId);
        assertFalse(storyLikesService.hasUserLiked(userId1, storyId));
        assertTrue(storyLikesService.hasUserLiked(userId2, storyId));
        assertEquals(1, storyLikesService.getLikeCount(storyId));
    }

    @Test
    @Transactional
    public void testGetAccurateLikeCount() {
        // Test data
        String userId = "test-user-202";
        Integer storyId = 6;

        // Initially no likes
        assertEquals(0, storyLikesService.getAccurateLikeCount(storyId));

        // Add a like
        storyLikesService.likeStory(userId, storyId);
        assertEquals(1, storyLikesService.getAccurateLikeCount(storyId));

        // Remove the like
        storyLikesService.unlikeStory(userId, storyId);
        assertEquals(0, storyLikesService.getAccurateLikeCount(storyId));
    }

    @Test
    public void testNullInputs() {
        // Test null userId
        assertFalse(storyLikesService.hasUserLiked(null, 1));
        assertEquals(0, storyLikesService.getLikeCount(null));
        assertEquals(0, storyLikesService.getAccurateLikeCount(null));

        // Test null storyId
        assertFalse(storyLikesService.hasUserLiked("user", null));
        assertEquals(0, storyLikesService.getLikeCount(null));
        assertEquals(0, storyLikesService.getAccurateLikeCount(null));
    }
} 