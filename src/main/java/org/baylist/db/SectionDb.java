package org.baylist.db;

import lombok.Data;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;

import java.util.List;

@Data
public class SectionDb {

    private Section section;
    private List<Task> tasks;

}
