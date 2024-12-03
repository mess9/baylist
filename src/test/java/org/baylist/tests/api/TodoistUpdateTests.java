package org.baylist.tests.api;

import org.assertj.core.api.Assertions;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TodoistUpdateTests {
    @Autowired
    TodoistController todoistController;

    @Test
    public void updateProject(){
        Project project = Project.builder()
                .setName("test")
                .build();

        Project testProject = todoistController.createProject(project);
        long idTestProject = Long.parseLong(testProject.getId());
        String newName = "Pupa";

        todoistController.updateProject(idTestProject, newName);

        Project updatedProject = todoistController.getProject(idTestProject);
        assertEquals(newName, updatedProject.getName());
    }

    @Test
    public void updateSection() {
        Project project = Project.builder()
                .setName("test")
                .build();
        Project testProject = todoistController.createProject(project);
        long idTestProject = Long.parseLong(testProject.getId());

        Section section = Section.builder()
                .setName("Test")
                .setProjectId(testProject.getId())
                .build();
        Section createdSection = todoistController.createSection(section);

        assertEquals("Test", createdSection.getName());

        List<Section> sectionsByProject = todoistController.getSectionsByProject(idTestProject);

        Section originalSection = sectionsByProject.stream()
                .filter(s -> s.getId().equals(createdSection.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Секция не найдена"));

        String newName = "Lupa";
        todoistController.updateSection(Long.parseLong(originalSection.getId()), newName);

        List<Section> updatedSections = todoistController.getSectionsByProject(idTestProject);

        Section updatedSection = updatedSections.stream()
                .filter(s -> s.getId().equals(originalSection.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Обновленная секция не найдена"));

        assertEquals(newName, updatedSection.getName());
    }

    @Test
    public void updateTask(){
        Project project = Project.builder()
                .setName("ЧТО-ТО")
                .build();
        Project testProject = todoistController.createProject(project);
        long idTestProject = Long.parseLong(testProject.getId());

        Task newTask = Task.builder()
                .xyiContent("Новая таска")
                .xyiProjectId(String.valueOf(idTestProject))
                .build();

        Task createdTask = todoistController.createTask(newTask);
        Assertions.assertThat(createdTask).isNotNull();

        String newContent = "ГАААААААЛЯ у нас замена";
        todoistController.updateTask(Long.parseLong(createdTask.getId()), newContent);

        List<Task> tasksByProject = todoistController.getTasksByProject(idTestProject);

        Task updatedTask = tasksByProject.stream()
                .filter(t -> t.getId().equals(createdTask.getId()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Нема Таски"));

        assertEquals(newContent, updatedTask.getContent());
    }
}
