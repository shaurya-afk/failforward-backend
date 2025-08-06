package com.failforward.deaddocs_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Integer storyId;
    private String userId;
    private String commenterName;
    private boolean isAnonymous;
    private String content;
} 