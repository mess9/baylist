package org.baylist.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Task;
import org.baylist.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.baylist.util.convert.ToJson.toJson;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class TaskController {

	TaskService taskService;

	@GetMapping("/tasks")
	public String tasks(@RequestParam String userId) {
		List<Task> tasksByUser;
		try {
			tasksByUser = taskService.getTasksByUser(userId);
			return toJson(tasksByUser);
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}

}
