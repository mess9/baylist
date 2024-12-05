package org.baylist.tests.api;

import org.baylist.tests.BaseTest;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistCreateTests extends BaseTest {

    @Autowired
    TodoistController todoistController;

    Project project;

    @BeforeEach
    void setUp() {
        project = todoistController.getProjects().getFirst();
    }

    @Test
    public void createProject() {
        Project project = Project.builder()
                .setName("test")
                .build();

        Project controllerProject = todoistController.createProject(project);

        assertThat(controllerProject).isNotNull();

        Project testProject = todoistController.getProject(Long.parseLong(controllerProject.getId()));
        assertThat(testProject).isEqualTo(controllerProject);
    }

    @Test
    public void createSection() {
        Section testSection = Section.builder()
                .setName("testSection")
                .setProjectId(project.getId())
                .build();
        Section section = todoistController.createSection(testSection);

        assertThat(section).isNotNull();

        List<Section> sectionsByProject = todoistController.getSectionsByProject(Long.parseLong(project.getId()));
        assertThat(sectionsByProject).contains(section);
    }

    @Test
    @DisplayName("проверка на хуй")
    public void createTaskAndDeleteTask() {
        Task xyi = Task.builder()
                .xyiProjectId(project.getId())
                .xyiContent("хуй")
                .build();

        Task task = todoistController.createTask(xyi);
        assertThat(task).isNotNull();


        List<Task> tasksByProject = todoistController.getTasksByProject(Long.parseLong(project.getId()));
        assertThat(tasksByProject).contains(task);
    }

}
