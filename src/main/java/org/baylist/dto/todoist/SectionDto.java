package org.baylist.dto.todoist;

import lombok.Data;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;

import java.util.List;

@Data
public class SectionDto {

    private Section section;
	private List<TaskResponse> tasks;

}
