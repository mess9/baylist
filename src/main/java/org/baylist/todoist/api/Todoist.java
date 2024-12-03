package org.baylist.todoist.api;

import org.baylist.todoist.dto.Label;
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

    List<Task> getTasksByLabel(String labels);

    List<Section> getSections();

    List<Section> getSectionsByProject(long index);

    List<Label> getLabels();

    //endregion GET

    // region CREATE

    Project createProject(Project project);

    Section createSection(Section section);

    Task createTask(Task task);

    //endregion CREATE

    // region DELETE

    void deleteProject(long projectId);
    void deleteSection(long sectionId);
    void deleteTask(long projectId);

    //endregion DELETE

    // region UPDATE

    void updateProject(long projectId, String newName);

    void updateSection(long sectionId, String newName);
    void updateTask(long taskId, String newContent);

    //endregion UPDATE

    void closeTask(long taskId);
    void reopenTask(long taskId);

}
