package org.baylist.todoist.api;

import org.baylist.dto.todoist.Label;
import org.baylist.dto.todoist.Project;
import org.baylist.dto.todoist.Section;
import org.baylist.dto.todoist.Task;

import java.util.List;

public interface Todoist {


    //todo
    // 1. создать имплементацию на контрактах
    // 2. создать имплементацию через sync api (но там уже не через этот интерфейс будет, а как-то иначе, хотя хз)


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

    void deleteTask(long taskId);

    //endregion DELETE

    // region UPDATE

    void updateProject(Project project);

    void updateSection(Section section);

    void updateTask(Task task);

    //endregion UPDATE

    void closeTask(long taskId);

    void reopenTask(long taskId);

}
