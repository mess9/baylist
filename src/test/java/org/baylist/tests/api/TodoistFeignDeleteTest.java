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

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistFeignDeleteTest extends BaseTest {

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
	void deleteProject() {
		Project project = Project.builder()
				.name("test")
				.build();
		Project controllerProject = todoistService.createProject(token, project);

		todoistApi.deleteProject(token, controllerProject.getId());

		assertThat(todoistApi.getProjects(token).stream().noneMatch(p -> controllerProject.getId().equals(p.getId())))
				.isTrue();
	}

	@Test
	void deleteSection() {
		Section section = todoistService
				.createSection(token, Section.builder().projectId(project.getId()).name("testSection").build());

		todoistApi.deleteSection(token, section.getId());

		assertThat(todoistApi
				.getSectionsByProject(token, project.getId())
				.stream()
				.noneMatch(p -> section.getId().equals(p.getId())))
				.isTrue();
	}

	@Test
	void deleteTask() {
		TaskResponse task = todoistService
				.createTask(token, TaskRequest.builder().projectId(project.getId()).content("content").build());

		todoistApi.deleteTask(token, task.getId());

		assertThat(todoistApi
				.getTasksByProject(token, project.getId())
				.stream().noneMatch(p -> task.getId().equals(p.getId())))
				.isTrue();

	}


}
