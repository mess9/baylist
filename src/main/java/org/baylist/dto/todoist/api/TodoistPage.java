package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoistPage<T> {

	private List<T> results;

	@JsonProperty("next_cursor")
	private String nextCursor;
}
