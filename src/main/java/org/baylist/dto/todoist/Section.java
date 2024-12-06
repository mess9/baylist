package org.baylist.dto.todoist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "set")
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
