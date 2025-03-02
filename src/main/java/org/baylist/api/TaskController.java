package org.baylist.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Task;
import org.baylist.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
	public String tasks() {
		List<Task> tasksByUser;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			tasksByUser = taskService.getTasksByUser(authentication.getPrincipal().toString());
			return toJson(tasksByUser);
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}

}
