package org.baylist.controller.todoist;

import org.baylist.dto.todoist.api.Label;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;

import java.util.List;

public interface Todoist {


    //todo
    // 1. создать имплементацию от контракта
    // 2. создать имплементацию через sync api (но там уже не через этот интерфейс будет, а как-то иначе, хотя хз)


    //region GET

    List<Project> getProjects();

    Project getProject(Long index);

    List<Task> getTasks();

    List<Task> getTasksByProject(Long index);

    List<Task> getTasksBySection(Long index);

    List<Task> getTasksByLabel(String labels);

    List<Section> getSections();

    List<Section> getSectionsByProject(Long index);

    List<Label> getLabels();

    //endregion GET

    // region CREATE

    Project createProject(Project project);

    Section createSection(Section section);

    Task createTask(Task task);

    //endregion CREATE

    // region DELETE

    void deleteProject(Long projectId);

    void deleteSection(Long sectionId);

    void deleteTask(Long taskId);

    //endregion DELETE

    // region UPDATE

    void updateProject(Project project);

    void updateSection(Section section);

    void updateTask(Task task);

    //endregion UPDATE

    void closeTask(Long taskId);

    void reopenTask(Long taskId);

}
