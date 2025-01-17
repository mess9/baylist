package org.baylist.dto.todoist;

import lombok.Data;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;

import java.util.List;

@Data
public class SectionDto {

    private Section section;
    private List<Task> tasks;

}
