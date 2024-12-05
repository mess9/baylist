package org.baylist.tests.api;

import org.baylist.tests.BaseTest;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TodoistUpdateTests extends BaseTest {

    @Autowired
    TodoistController todoistController;

    Project project;

    @BeforeEach
    void setUp() {
        project = todoistController.getProjects().getFirst();
    }

    @Test
    public void updateProject() {
        Project pupa = todoistController.createProject(Project.builder().setName("pupa").build());
        pupa.setName("Lupa");

        todoistController.updateProject(pupa);

        Project updatedProject = todoistController.getProject(Long.parseLong(pupa.getId()));
        assertThat(pupa.getName()).isEqualTo(updatedProject.getName());
    }

    @Test
    public void updateSection() {
        Section section = Section.builder()
                .setName("Test")
                .setProjectId(project.getId())
                .build();
        Section createdSection = todoistController.createSection(section);

        assertThat(createdSection.getName()).isEqualTo("Test");

        List<Section> sectionsByProject = todoistController.getSectionsByProject(Long.parseLong(project.getId()));

        Section originalSection = sectionsByProject.stream()
                .filter(s -> s.getId().equals(createdSection.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Секция не найдена"));
        originalSection.setName("Parabaloid");
        todoistController.updateSection(originalSection);

        List<Section> updatedSections = todoistController.getSectionsByProject(Long.parseLong(project.getId()));

        Section updatedSection = updatedSections.stream()
                .filter(s -> s.getId().equals(originalSection.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Обновленная секция не найдена"));

        assertThat(originalSection.getName()).isEqualTo(updatedSection.getName());
    }

    @Test
    public void updateTask() {
        Task newTask = Task.builder()
                .xyiContent("Новая таска")
                .xyiProjectId(String.valueOf(project.getId()))
                .build();

        Task createdTask = todoistController.createTask(newTask);
        assertThat(createdTask).isNotNull();

        createdTask.setContent("ГАААААААЛЯ у нас замена");
        todoistController.updateTask(createdTask);

        List<Task> tasksByProject = todoistController.getTasksByProject(Long.parseLong(project.getId()));

        Task updatedTask = tasksByProject.stream()
                .filter(t -> t.getId().equals(createdTask.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Нема Таски"));

        assertThat(createdTask.getContent()).isEqualTo(updatedTask.getContent());
    }
}
