package org.baylist.tests.api;

import org.baylist.api.TodoistFeignClient;
import org.baylist.dto.todoist.api.Label;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;
import org.baylist.tests.BaseTest;
import org.baylist.util.extension.FilToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TodoistFeignGetTest extends BaseTest {

	@Autowired
	TodoistFeignClient todoistClient;

	@FilToken
	String token;


	@Test
	void getAllProjects() {
		List<Project> projects = todoistClient.getProjects(token);

		assertThat(projects)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getProjectById() {
		List<Project> projects = todoistClient.getProjects(token);
		System.out.println(projects);
		var projectId = projects.getFirst().getId();
		Project project = todoistClient.getProject(token, projectId);

		assertThat(project).isNotNull();
	}

	@Test
	void getAllOpenTasks() {
		List<TaskResponse> tasks = todoistClient.getTasks(token);

		assertThat(tasks)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getTaskByProjectId() {
		List<TaskResponse> tasks = todoistClient.getTasks(token);
		List<TaskResponse> tasksByProject = todoistClient.getTasksByProject(token, tasks.getFirst().getProjectId());

		assertThat(tasksByProject)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getTaskBySectionId() {
		List<TaskResponse> tasks = todoistClient.getTasks(token);
		List<TaskResponse> tasksBySection = todoistClient.getTasksBySection(token, tasks.getFirst().getSectionId());

		assertThat(tasksBySection)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getTaskByLabel() {
		List<TaskResponse> tasks = todoistClient.getTasks(token);
		List<TaskResponse> tasksByLabel = todoistClient.getTasksByLabel(token,
				tasks.stream().filter(e -> !e.getLabels().isEmpty()).findAny().orElseThrow().getLabels().getFirst()
		);

		assertThat(tasksByLabel)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getAllSections() {
		List<Section> sections = todoistClient.getSections(token);

		assertThat(sections)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getSectionByProjectId() {
		List<Section> sections = todoistClient.getSections(token);
		List<Section> sectionsByProjectId = todoistClient.getSectionsByProject(token, sections.getFirst().getProjectId());

		assertThat(sectionsByProjectId)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

	@Test
	void getLabel() {
		List<Label> labels = todoistClient.getLabels(token);

		assertThat(labels)
				.isNotEmpty()
				.hasSizeGreaterThan(1);
	}

}
