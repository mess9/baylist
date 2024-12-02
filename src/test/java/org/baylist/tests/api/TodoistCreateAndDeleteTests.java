package org.baylist.tests.api;

import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TodoistCreateAndDeleteTests {

    @Autowired
    TodoistController todoistController;

    @Test
    public void createProjectAndDeleteProject() {
        Project project = Project.builder()
                .setName("test")
                .build();

        Project controllerProject = todoistController.createProject(project);

        assertThat(controllerProject).isNotNull();

        Project testProject = todoistController.getProject(Long.parseLong(controllerProject.getId()));
        assertThat(testProject).isEqualTo(controllerProject);

        todoistController.deleteProject(Long.parseLong(testProject.getId()));
        List<Project> projects = todoistController.getProjects();
        assertThat(projects.stream().filter(p -> p.getId().equals(testProject.getId()))).isEmpty();

    }

    @Test
    public void createSectionAndDeleteSection() {
        String projectId = todoistController.getProjects().getFirst().getId();
        Section testSection = Section.builder()
                .setName("testSection")
                .setProjectId(projectId)
                .build();
        Section section = todoistController.createSection(testSection);

        assertThat(section).isNotNull();

        List<Section> sectionsByProject = todoistController.getSectionsByProject(Long.parseLong(projectId));
        assertThat(sectionsByProject).contains(section);

        todoistController.deleteSection(Long.parseLong(section.getId()));
        List<Section> sections = todoistController.getSections();
        assertThat(sections).doesNotContain(section);
    }

    @Test
    @DisplayName("проверка на хуй")
    public void createTaskAndDeleteTask() {
        Task xyi = Task.builder()
                .xyiContent("хуй")
                .build();

        Task task = todoistController.createTask(xyi);
        assertThat(task).isNotNull();

        String inboxProjectId = todoistController.getProjects().stream().filter(p -> p.getName().equals("Inbox")).findAny().orElseThrow().getId();
        List<Task> tasksByProject = todoistController.getTasksByProject(Long.parseLong(inboxProjectId));
        assertThat(tasksByProject).contains(task);

        todoistController.deleteTask(Long.parseLong(task.getId()));
        List<Task> tasksByProjectNext = todoistController.getTasksByProject(Long.parseLong(inboxProjectId));
        assertThat(tasksByProjectNext).doesNotContain(task);
    }

}
