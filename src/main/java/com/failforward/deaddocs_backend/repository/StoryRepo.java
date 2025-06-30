package com.failforward.deaddocs_backend.repository;

import com.failforward.deaddocs_backend.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepo extends JpaRepository<Story, Integer> {
    Story getStoriesByUserId(String userId);

    void deleteStoryById(Integer id);

    List<Story> findAllByUserId(String userId);
}
