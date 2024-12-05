package org.baylist.tests.api;

import org.baylist.tests.BaseTest;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistDeleteTest extends BaseTest {

    @Autowired
    TodoistController todoistController;

    Project project;

    @BeforeEach
    void setUp() {
        project = todoistController.getProjects().getFirst();
    }

    @Test
    void deleteProject() {
        Project project = Project.builder()
                .setName("test")
                .build();
        Project controllerProject = todoistController.createProject(project);

        todoistController.deleteProject(Long.parseLong(controllerProject.getId()));

        assertThat(todoistController.getProjects().stream().noneMatch(p -> controllerProject.getId().equals(p.getId())))
                .isTrue();
    }

    @Test
    void deleteSection() {
        Section section = todoistController
                .createSection(Section.builder().setProjectId(project.getId()).setName("testSection").build());

        todoistController.deleteSection(Long.parseLong(section.getId()));

        assertThat(todoistController
                .getSectionsByProject(Long.parseLong(project.getId()))
                .stream()
                .noneMatch(p -> section.getId().equals(p.getId())))
                .isTrue();
    }

    @Test
    void deleteTask() {
        Task task = todoistController
                .createTask(Task.builder().xyiProjectId(project.getId()).xyiContent("content").build());

        todoistController.deleteTask(Long.parseLong(task.getId()));

        assertThat(todoistController
                .getTasksByProject(Long.parseLong(project.getId()))
                .stream().noneMatch(p -> task.getId().equals(p.getId())))
                .isTrue();

    }


}
