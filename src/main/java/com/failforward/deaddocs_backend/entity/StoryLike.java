package com.failforward.deaddocs_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "story_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "story_id"}))
public class StoryLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "story_id", nullable = false)
    private Integer storyId;
}
