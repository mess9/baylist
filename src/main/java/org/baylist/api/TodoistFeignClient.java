package org.baylist.api;

import org.baylist.dto.todoist.api.Label;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskRequest;
import org.baylist.dto.todoist.api.TaskResponse;
import org.baylist.dto.todoist.api.TodoistPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@FeignClient(name = "todoistClient", url = "${todoist.baseUrl}")
public interface TodoistFeignClient {

	String AUTHORIZATION = "Authorization";
	int PAGE_LIMIT = 200;

	String PROJECT_METHOD = "/projects";
	String PROJECT_ID_METHOD = "/projects/{projectId}";

	String TASK_METHOD = "tasks";
	String TASK_ID_METHOD = "tasks/{taskId}";
	String TASK_ID_CLOSE_METHOD = "tasks/{taskId}/close";
	String TASK_ID_REOPEN_METHOD = "tasks/{taskId}/reopen";

	String SECTION_METHOD = "sections";
	String SECTION_ID_METHOD = "sections/{sectionId}";

	String LABEL_METHOD = "labels";


	//region GET

	@GetMapping(PROJECT_METHOD)
	TodoistPage<Project> getProjectsPage(@RequestHeader(AUTHORIZATION) String token,
	                                     @RequestParam(required = false) String cursor,
	                                     @RequestParam(required = false) Integer limit);

	default List<Project> getProjects(String token) {
		return collectAllPages(cursor -> getProjectsPage(token, cursor, PAGE_LIMIT));
	}

	@GetMapping(PROJECT_ID_METHOD)
	Project getProject(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String projectId);

	@GetMapping(TASK_METHOD)
	TodoistPage<TaskResponse> getTasksPage(@RequestHeader(AUTHORIZATION) String token,
	                                       @RequestParam(required = false) String cursor,
	                                       @RequestParam(required = false) Integer limit);

	default List<TaskResponse> getTasks(String token) {
		return collectAllPages(cursor -> getTasksPage(token, cursor, PAGE_LIMIT));
	}

	@GetMapping(TASK_METHOD)
	TodoistPage<TaskResponse> getTasksByProjectPage(@RequestHeader(AUTHORIZATION) String token,
	                                                @RequestParam("project_id") String projectId,
	                                                @RequestParam(required = false) String cursor,
	                                                @RequestParam(required = false) Integer limit);

	default List<TaskResponse> getTasksByProject(String token, String projectId) {
		return collectAllPages(cursor -> getTasksByProjectPage(token, projectId, cursor, PAGE_LIMIT));
	}

	@GetMapping(TASK_METHOD)
	TodoistPage<TaskResponse> getTasksBySectionPage(@RequestHeader(AUTHORIZATION) String token,
	                                                @RequestParam("section_id") String sectionId,
	                                                @RequestParam(required = false) String cursor,
	                                                @RequestParam(required = false) Integer limit);

	default List<TaskResponse> getTasksBySection(String token, String sectionId) {
		return collectAllPages(cursor -> getTasksBySectionPage(token, sectionId, cursor, PAGE_LIMIT));
	}

	@GetMapping(TASK_METHOD)
	TodoistPage<TaskResponse> getTasksByLabelPage(@RequestHeader(AUTHORIZATION) String token,
	                                              @RequestParam("label") String label,
	                                              @RequestParam(required = false) String cursor,
	                                              @RequestParam(required = false) Integer limit);

	default List<TaskResponse> getTasksByLabel(String token, String label) {
		return collectAllPages(cursor -> getTasksByLabelPage(token, label, cursor, PAGE_LIMIT));
	}

	@GetMapping(SECTION_METHOD)
	TodoistPage<Section> getSectionsPage(@RequestHeader(AUTHORIZATION) String token,
	                                     @RequestParam(required = false) String cursor,
	                                     @RequestParam(required = false) Integer limit);

	default List<Section> getSections(String token) {
		return collectAllPages(cursor -> getSectionsPage(token, cursor, PAGE_LIMIT));
	}

	@GetMapping(SECTION_METHOD)
	TodoistPage<Section> getSectionsByProjectPage(@RequestHeader(AUTHORIZATION) String token,
	                                              @RequestParam("project_id") String projectId,
	                                              @RequestParam(required = false) String cursor,
	                                              @RequestParam(required = false) Integer limit);

	default List<Section> getSectionsByProject(String token, String projectId) {
		return collectAllPages(cursor -> getSectionsByProjectPage(token, projectId, cursor, PAGE_LIMIT));
	}

	@GetMapping(LABEL_METHOD)
	TodoistPage<Label> getLabelsPage(@RequestHeader(AUTHORIZATION) String token,
	                                 @RequestParam(required = false) String cursor,
	                                 @RequestParam(required = false) Integer limit);

	default List<Label> getLabels(String token) {
		return collectAllPages(cursor -> getLabelsPage(token, cursor, PAGE_LIMIT));
	}

	//endregion GET

	//region CREATE

	@PostMapping(PROJECT_METHOD)
	Project createProject(@RequestHeader(AUTHORIZATION) String token,
	                      @RequestBody Project project);

	@PostMapping(SECTION_METHOD)
	Section createSection(@RequestHeader(AUTHORIZATION) String token,
	                      @RequestBody Section section);

	@PostMapping(TASK_METHOD)
	TaskResponse createTask(@RequestHeader(AUTHORIZATION) String token,
	                        @RequestBody TaskRequest task);

	//endregion CREATE

	//region DELETE

	@DeleteMapping(PROJECT_ID_METHOD)
	void deleteProject(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String projectId);

	@DeleteMapping(SECTION_ID_METHOD)
	void deleteSection(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String sectionId);

	@DeleteMapping(TASK_ID_METHOD)
	void deleteTask(@RequestHeader(AUTHORIZATION) String token,
	                @PathVariable String taskId);


	//endregion DELETE

	//region UPDATE

	@PostMapping(PROJECT_ID_METHOD)
	void updateProject(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String projectId,
	                   @RequestBody Project project);

	@PostMapping(SECTION_ID_METHOD)
	void updateSection(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String sectionId,
	                   @RequestBody Section section);

	@PostMapping(TASK_ID_METHOD)
	void updateTask(@RequestHeader(AUTHORIZATION) String token,
	                @PathVariable String taskId,
	                @RequestBody TaskResponse task);


	//endregion UPDATE

	@PostMapping(TASK_ID_CLOSE_METHOD)
	void closeTask(@RequestHeader(AUTHORIZATION) String token,
	               @PathVariable String taskId);

	@PostMapping(TASK_ID_REOPEN_METHOD)
	void reopenTask(@RequestHeader(AUTHORIZATION) String token,
	                @PathVariable String taskId);

	private static <T> List<T> collectAllPages(Function<String, TodoistPage<T>> pageFetcher) {
		List<T> results = new ArrayList<>();
		String cursor = null;
		while (true) {
			TodoistPage<T> page = pageFetcher.apply(cursor);
			if (page == null) {
				break;
			}
			if (page.getResults() != null) {
				results.addAll(page.getResults());
			}
			String nextCursor = page.getNextCursor();
			if (nextCursor == null || nextCursor.isBlank() || nextCursor.equals(cursor)) {
				break;
			}
			cursor = nextCursor;
		}
		return results;
	}

}
