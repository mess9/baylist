package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //Возможно это костыль
public class Project {

    @JsonCreator
    //чёт джексон закусился на этом методе, говоря что нет конструктора по умолчанию, но добавить его нельзя, тогда билдер ломается. пришлось так
    public Project(@JsonProperty("id") String id, @JsonProperty("parent_id") String parentId, @JsonProperty("order") Integer order, @JsonProperty("color") String color, @JsonProperty("name") String name, @JsonProperty("comment_count") Integer commentCount, @JsonProperty("is_shared") Boolean isShared, @JsonProperty("is_favorite") Boolean isFavorite, @JsonProperty("is_inbox_project") Boolean isInboxProject, @JsonProperty("is_team_inbox") Boolean isTeamInbox, @JsonProperty("url") String url, @JsonProperty("view_style") String viewStyle) {
        this.id = id;
        this.parentId = parentId;
        this.order = order;
        this.color = color;
        this.name = name;
        this.commentCount = commentCount;
        this.isShared = isShared;
        this.isFavorite = isFavorite;
        this.isInboxProject = isInboxProject;
        this.isTeamInbox = isTeamInbox;
        this.url = url;
        this.viewStyle = viewStyle;
    }

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

}
