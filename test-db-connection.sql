-- Test script to verify NeonDB connection and like functionality
-- This script can be run against the NeonDB to verify the setup

-- Check if tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('stories', 'story_likes');

-- Check stories table structure
SELECT column_name, data_type, is_nullable
FROM information_schema.columns 
WHERE table_name = 'stories' 
AND table_schema = 'public'
ORDER BY ordinal_position;

-- Check story_likes table structure
SELECT column_name, data_type, is_nullable
FROM information_schema.columns 
WHERE table_name = 'story_likes' 
AND table_schema = 'public'
ORDER BY ordinal_position;

-- Check unique constraints on story_likes
SELECT constraint_name, constraint_type
FROM information_schema.table_constraints 
WHERE table_name = 'story_likes' 
AND table_schema = 'public';

-- Check current data (if any)
SELECT 'stories' as table_name, COUNT(*) as row_count FROM stories
UNION ALL
SELECT 'story_likes' as table_name, COUNT(*) as row_count FROM story_likes;

-- Test like count query
SELECT 
    s.id,
    s.story_title,
    s.helpful_votes as story_helpful_votes,
    COUNT(sl.id) as actual_like_count,
    CASE 
        WHEN s.helpful_votes = COUNT(sl.id) THEN 'Consistent'
        ELSE 'Inconsistent'
    END as consistency_status
FROM stories s
LEFT JOIN story_likes sl ON s.id = sl.story_id
GROUP BY s.id, s.story_title, s.helpful_votes
ORDER BY s.id; 