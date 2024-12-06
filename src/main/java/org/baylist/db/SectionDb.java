package org.baylist.db;

import lombok.Data;
import org.baylist.dto.todoist.Section;
import org.baylist.dto.todoist.Task;

import java.util.List;

@Data
public class SectionDb {

    private Section section;
    private List<Task> tasks;

}
