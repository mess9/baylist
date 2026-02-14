package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponse {

	private String id;
	@JsonProperty("assigned_by_uid")
	@JsonAlias("assigner_id")
	private String assignerId;
	@JsonProperty("responsible_uid")
	@JsonAlias("assignee_id")
	private String assigneeId;
	@JsonProperty("project_id")
	private String projectId;
	@JsonProperty("section_id")
	private String sectionId;
	@JsonProperty("parent_id")
	private String parentId;
	@JsonAlias("child_order")
	private int order;
	private String content;
	private String description;
	@JsonProperty("checked")
	@JsonAlias("is_completed")
	private boolean isCompleted;
	private List<String> labels;
	private int priority;
	@JsonProperty("note_count")
	@JsonAlias("comment_count")
	private int commentCount;
	@JsonProperty("added_by_uid")
	@JsonAlias("creator_id")
	private String creatorId;
	@JsonProperty("added_at")
	@JsonAlias("created_at")
	private OffsetDateTime createdAt;
	private Due due;
	private String url;
	private Duration duration;
	private Deadline deadline;

	enum Unit {
		minute, day
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Due {

		private String date;
		@JsonProperty("is_recurring")
		private boolean isRecurring;
		@JsonProperty("datetime")
		private OffsetDateTime dateTime;
		private String string;
		private String timezone;
		private String lang;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Duration {

		private Integer amount;
		private Unit unit;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class Deadline {

		private String date;
		private OffsetDateTime datetime;
		private String timezone;
		private String lang;
	}

}
