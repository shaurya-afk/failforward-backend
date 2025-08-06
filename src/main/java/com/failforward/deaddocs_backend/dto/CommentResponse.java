package com.failforward.deaddocs_backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private Integer storyId;
    private String userId;
    private String displayName;
    private boolean isAnonymous;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String commenterName;

    public String getCommenterName() {
        return commenterName;
    }

    public String getDisplayName() {
        if (isAnonymous) {
            return "Anonymous";
        }
        return commenterName != null ? commenterName : "Anonymous";
    }
} 