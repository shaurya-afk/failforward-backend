package com.failforward.deaddocs_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StoryRequest {
    private String userId;
    private String founderName;
    private boolean isAnonymous;
    private String storyTitle;
    private String industry;
    private String previewText;
    private int helpfulVotes;
    private int commentCount;
}
