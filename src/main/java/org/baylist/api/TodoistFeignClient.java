package org.baylist.api;

import org.baylist.dto.todoist.api.Label;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskRequest;
import org.baylist.dto.todoist.api.TaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "todoistClient", url = "${todoist.baseUrl}")
public interface TodoistFeignClient {

	String AUTHORIZATION = "Authorization";

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
	List<Project> getProjects(@RequestHeader(AUTHORIZATION) String token);

	@GetMapping(PROJECT_ID_METHOD)
	Project getProject(@RequestHeader(AUTHORIZATION) String token,
	                   @PathVariable String projectId);

	@GetMapping(TASK_METHOD)
	List<TaskResponse> getTasks(@RequestHeader(AUTHORIZATION)
	                    String token);

	@GetMapping(TASK_METHOD)
	List<TaskResponse> getTasksByProject(@RequestHeader(AUTHORIZATION) String token,
	                                     @RequestParam String project_id);

	@GetMapping(TASK_METHOD)
	List<TaskResponse> getTasksBySection(@RequestHeader(AUTHORIZATION) String token,
	                                     @RequestParam String section_id);

	@GetMapping(TASK_METHOD)
	List<TaskResponse> getTasksByLabel(@RequestHeader(AUTHORIZATION) String token,
	                                   @RequestParam String label);

	@GetMapping(SECTION_METHOD)
	List<Section> getSections(@RequestHeader(AUTHORIZATION) String token);

	@GetMapping(SECTION_METHOD)
	List<Section> getSectionsByProject(@RequestHeader(AUTHORIZATION) String token,
	                                   @RequestParam String project_id);

	@GetMapping(LABEL_METHOD)
	List<Label> getLabels(@RequestHeader(AUTHORIZATION) String token);

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


}
