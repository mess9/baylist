package org.baylist.todoist.api;

import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;

import java.util.List;

public interface Todoist {


    //region GET

    List<Project> getProjects();

    Project getProject(long index);

    List<Task> getTasks();

    List<Task> getTasksByProject(long index);

    List<Task> getTasksBySection(long index);

    List<Task> getTasksByLabel(List<String> labels);

    List<Task> getTasksByLabel(String labels);

    List<Section> getAllSections();

    List<Section> getSectionsByProject(long index);

    //endregion GET

}
