package org.baylist.db;

import lombok.Data;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Task;

import java.util.List;

@Data
public class ProjectDb {

    private Project project;
    private List<Task> tasks;
    private List<SectionDb> sections;


}
