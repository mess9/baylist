package org.baylist.todoist.dto;

import lombok.Data;

@Data
public class Project {
    private String id;
    private String parentId;
    private int order;
    private String color;
    private String name;
    private int commentCount;
    private boolean isShared;
    private boolean isFavorite;
    private boolean isInboxProject;
    private boolean isTeamInbox;
    private String url;
    private String viewStyle;
}
