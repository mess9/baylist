package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Section {
    private String id;
    @JsonProperty("v2_id")
    private String v2Id;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("v2_project_id")
    private String v2ProjectId;
	@JsonProperty("section_order")
    private int order;
    private String name;
}
