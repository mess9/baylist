package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskRequest {

    @JsonProperty("project_id")
    String projectId;
    @JsonProperty("section_id")
    String sectionId;
    @JsonProperty("parent_id")
    String parentId;
	int order;
	String content;
	String description;

	List<String> labels;
	int priority;

	String due_datetime;

}