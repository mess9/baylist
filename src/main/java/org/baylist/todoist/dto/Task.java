package org.baylist.todoist.dto;

import lombok.Data;

import java.util.List;

@Data
public class Task {
    private String id;
    private String assignerId;
    private String assigneeId;
    private String projectId;
    private String sectionId;
    private String parentId;
    private int order;
    private String content;
    private String description;
    private boolean isCompleted;
    private List<String> labels;
    private int priority;
    private int commentCount;
    private String creatorId;
    private String createdAt;
    private String due;
    private String url;
    private String duration;
    private String deadline;
}