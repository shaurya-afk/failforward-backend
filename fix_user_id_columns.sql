-- Migration script to fix userId column sizes
-- Run this script on your database to resolve the "value too long for type character varying(255)" error

-- Fix userId column in story_likes table
ALTER TABLE story_likes 
ALTER COLUMN "userId" TYPE VARCHAR(1000);

-- Fix user_id column in stories table  
ALTER TABLE stories 
ALTER COLUMN user_id TYPE VARCHAR(1000);

-- Verify the changes
SELECT 
    table_name, 
    column_name, 
    data_type, 
    character_maximum_length
FROM information_schema.columns 
WHERE table_name IN ('story_likes', 'stories') 
AND column_name IN ('userId', 'user_id')
ORDER BY table_name, column_name; 