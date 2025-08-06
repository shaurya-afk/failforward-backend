package com.failforward.deaddocs_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Table(name = "stories")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(name = "founder_name", length = 255)
    private String founderName;

    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Column(name = "story_title", length = 255)
    private String storyTitle;

    @Column(name = "industry", length = 255)
    private String industry;

    @Column(name = "preview_text", length = 1000)
    private String previewText;

    @Column(name = "helpful_votes")
    private int helpfulVotes;

    //TODO(add a thread of comments)
    @Column(name = "comment_count")
    private int commentCount;
}
