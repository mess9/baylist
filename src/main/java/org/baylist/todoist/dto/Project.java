package org.baylist.todoist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "set")
public class Project {
    private String id;
    @JsonProperty("parent_id")
    private String parentId;
    private int order;
    private String color;
    private String name;
    @JsonProperty("comment_count")
    private int commentCount;
    @JsonProperty("is_shared")
    private boolean isShared;
    @JsonProperty("is_favorite")
    private boolean isFavorite;
    @JsonProperty("is_inbox_project")
    private boolean isInboxProject;
    @JsonProperty("is_team_inbox")
    private boolean isTeamInbox;
    private String url;
    @JsonProperty("view_style")

    private String viewStyle;
}
