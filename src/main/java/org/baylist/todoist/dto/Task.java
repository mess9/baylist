package org.baylist.todoist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(setterPrefix = "xyi")
public class Task {
    private String id;
    @JsonProperty("assigner_id")
    private String assignerId;
    @JsonProperty("assignee_id")
    private String assigneeId;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("section_id")
    private String sectionId;
    @JsonProperty("parent_id")
    private String parentId;
    private int order;
    private String content;
    private String description;
    @JsonProperty("is_completed")
    private boolean isCompleted;
    private List<String> labels;
    private int priority;
    @JsonProperty("comment_count")
    private int commentCount;
    @JsonProperty("creator_id")
    private String creatorId;
    @JsonProperty("created_at")
    private LocalDateTime createdAt; //todo разобраться с вопросом часовых поясов и таймзон
    private Due due;
    private String url;
    private Duration duration;
    private String deadline;

    enum Unit {
        minute, day
    }

    @Data
    static class Due {
        private String date;
        @JsonProperty("is_recurring")
        private boolean isRecurring;
        @JsonProperty("datetime")
        private LocalDateTime dateTime;
        private String string;
        private String timezone;
        private String lang;
    }

    @Data
    static class Duration {
        private Integer amount;
        private Unit unit;
    }

}