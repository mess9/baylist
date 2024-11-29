package org.baylist.todoist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Section {
    private String id;
    @JsonProperty("v2_id")
    private String v2Id;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("v2_project_id")
    private String v2ProjectId;
    private int order;
    private String name;
}
