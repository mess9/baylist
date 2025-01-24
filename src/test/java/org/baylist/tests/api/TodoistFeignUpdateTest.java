package org.baylist.tests.api;

import org.baylist.api.TodoistFeignClient;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskRequest;
import org.baylist.dto.todoist.api.TaskResponse;
import org.baylist.service.TodoistService;
import org.baylist.tests.BaseTest;
import org.baylist.util.extension.FilToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistFeignUpdateTest extends BaseTest {

	@Autowired
	TodoistService todoistService;

	@Autowired
	TodoistFeignClient todoistApi;

	@FilToken
	String token;

	private Project project;

	@BeforeEach
	void setUp() {
		project = todoistApi.getProjects(token).getFirst();
	}

	@Test
//    @Disabled("тест регулярно падает, надо бы починить")
	public void updateProject() {
		Project pupa = todoistService.createProject(token, Project.builder().name("pupa").build());
		pupa.setName("Lupa");

		todoistApi.updateProject(token, pupa.getId(), pupa);

		Project updatedProject = todoistApi.getProject(token, pupa.getId());
		assertThat(pupa.getName()).isEqualTo(updatedProject.getName());
	}

	@Test
	public void updateSection() {
		Section section = Section.builder()
				.name("Test")
				.projectId(project.getId())
				.build();
		Section createdSection = todoistService.createSection(token, section);

		assertThat(createdSection.getName()).isEqualTo("Test");

		List<Section> sectionsByProject = todoistApi.getSectionsByProject(token, project.getId());

		Section originalSection = sectionsByProject.stream()
				.filter(s -> s.getId().equals(createdSection.getId()))
				.findAny()
				.orElseThrow(() -> new AssertionError("Секция не найдена"));
		originalSection.setName("Parabaloid");
		todoistApi.updateSection(token, originalSection.getId(), originalSection);

		List<Section> updatedSections = todoistApi.getSectionsByProject(token, project.getId());

		Section updatedSection = updatedSections.stream()
				.filter(s -> s.getId().equals(originalSection.getId()))
				.findAny()
				.orElseThrow(() -> new AssertionError("Обновленная секция не найдена"));

		assertThat(originalSection.getName()).isEqualTo(updatedSection.getName());
	}

	@Test
	public void updateTask() {
		TaskRequest newTask = TaskRequest.builder()
				.content("Новая таска")
				.projectId(String.valueOf(project.getId()))
				.build();

		TaskResponse createdTask = todoistService.createTask(token, newTask);
		assertThat(createdTask).isNotNull();

		createdTask.setContent("ГАААААААЛЯ у нас замена");
		todoistApi.updateTask(token, createdTask.getId(), createdTask);

		List<TaskResponse> tasksByProject = todoistApi.getTasksByProject(token, project.getId());

		TaskResponse updatedTask = tasksByProject.stream()
				.filter(t -> t.getId().equals(createdTask.getId()))
				.findAny()
				.orElseThrow(() -> new AssertionError("Нема Таски"));

		assertThat(createdTask.getContent()).isEqualTo(updatedTask.getContent());
	}
}
