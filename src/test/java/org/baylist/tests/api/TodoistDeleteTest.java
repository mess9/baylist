package org.baylist.tests.api;

import org.baylist.dto.todoist.Project;
import org.baylist.dto.todoist.Section;
import org.baylist.dto.todoist.Task;
import org.baylist.tests.BaseTest;
import org.baylist.todoist.api.Todoist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistDeleteTest extends BaseTest {

    @Autowired
    Todoist todoistController;

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
