package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Project {

    private String id;
    @JsonProperty("parent_id")
    private String parentId;
    private Integer order;
    private String color;
    private String name;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("is_shared")
    private Boolean isShared;
    @JsonProperty("is_favorite")
    private Boolean isFavorite;
    @JsonProperty("is_inbox_project")
    private Boolean isInboxProject;
    @JsonProperty("is_team_inbox")
    private Boolean isTeamInbox;
    private String url;
    @JsonProperty("view_style")
    private String viewStyle;
    private String description;

}
