package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class Section {
    private String id;
    @JsonProperty("v2_id")
    private String v2Id;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("v2_project_id")
    private String v2ProjectId;
    private Integer order;
    private String name;
}
