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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistFeignCreateTest extends BaseTest {

	@Autowired
	TodoistFeignClient todoistApi;

	@Autowired
	TodoistService todoistService;

	Project project;

	@FilToken
	String token;

	@BeforeEach
	void setUp() {
		project = todoistApi.getProjects(token).getFirst();
	}


	@Test
	public void createProject() {
		Project project = Project.builder()
				.name("test")
				.build();

		Project controllerProject = todoistService.createProject(token, project);

		assertThat(controllerProject).isNotNull();

		Project testProject = todoistApi.getProject(token, controllerProject.getId());
		assertThat(testProject).isEqualTo(controllerProject);
	}

	@Test
	public void createSection() {
		Section testSection = Section.builder()
				.name("testSection")
				.projectId(project.getId())
				.build();
		Section section = todoistService.createSection(token, testSection);

		assertThat(section).isNotNull();

		List<Section> sectionsByProject = todoistApi.getSectionsByProject(token, project.getId());
		assertThat(sectionsByProject).contains(section);
	}

	@Test
	@DisplayName("проверка на хуй")
	public void createTaskAndDeleteTask() {
		TaskRequest xyi = TaskRequest.builder()
				.projectId(project.getId())
				.content("хуй")
				.build();

		TaskResponse task = todoistService.createTask(token, xyi);

		assertThat(task).isNotNull();

		List<TaskResponse> tasksByProject = todoistApi.getTasksByProject(token, project.getId());
		assertThat(tasksByProject).contains(task);
	}

}
