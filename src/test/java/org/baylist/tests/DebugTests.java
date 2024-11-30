package org.baylist.tests;

import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Label;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DebugTests {

    @Autowired
    TodoistController todoistController;

    @Test
    public void getAllProjects() {
        List<Project> projects = todoistController.getProjects();

        assertThat(projects)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getProjectById() {
        List<Project> projects = todoistController.getProjects();
        Project project = todoistController.getProject(Long.parseLong(projects.getFirst().getId()));

        assertThat(project).isNotNull();
    }

    @Test
    public void getAllOpenTasks() {
        List<Task> tasks = todoistController.getTasks();

        assertThat(tasks)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getTaskByProjectId() {
        List<Task> tasks = todoistController.getTasks();
        List<Task> tasksByProject = todoistController.getTasksByProject(Long.parseLong(tasks.getFirst().getProjectId()));

        assertThat(tasksByProject)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getTaskBySectionId() {
        List<Task> tasks = todoistController.getTasks();
        List<Task> tasksBySection = todoistController.getTasksBySection(Long.parseLong(tasks.getFirst().getSectionId()));

        assertThat(tasksBySection)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getTaskByLabel() {
        List<Task> tasks = todoistController.getTasks();
        List<Task> tasksByLabel = todoistController.getTasksByLabel(
                tasks.stream().filter(e -> !e.getLabels().isEmpty()).findAny().orElseThrow().getLabels().getFirst()
        );

        assertThat(tasksByLabel)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getAllSections() {
        List<Section> sections = todoistController.getSections();

        assertThat(sections)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getSectionByProjectId() {
        List<Section> sections = todoistController.getSections();
        System.out.println(sections);
        List<Section> sectionsByProjectId = todoistController.getSectionsByProject(Long.parseLong(sections.getFirst().getProjectId()));

        assertThat(sectionsByProjectId)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getLabel() {
        List<Label> labels = todoistController.getLabels();

        assertThat(labels)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }




}
