package org.baylist.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Task;
import org.baylist.db.entity.User;
import org.baylist.db.repo.TaskRepository;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TaskService {

	TaskRepository taskRepository;
	UserService userService;

	public List<Task> getTasksByUser(String userId) {
		User byUserId = userService.findByUserId(Long.parseLong(userId));
		return taskRepository.findByUser(byUserId);
	}


	@Transactional
	public void syncTasks(User owner, List<Section> sections, List<TaskResponse> tasks) {
		Map<String, String> sectionsIdNameMap = sections.stream()
				.collect(Collectors.toMap(Section::getId, Section::getName));
		List<Task> existTasks = taskRepository.findByUser(owner);
		Map<String, Task> existTasksMap = existTasks.stream()
				.collect(Collectors.toMap(Task::getContent, task -> task, (task1, task2) -> task1
				));
		List<Task> tasksToSave = new ArrayList<>();

		// Обрабатываем задачи с удаленной стороны
		for (TaskResponse remoteTask : tasks) {
			String sectionName = sectionsIdNameMap.getOrDefault(remoteTask.getSectionId(), UNKNOWN_CATEGORY);
			Task localTask = existTasksMap.get(remoteTask.getContent());

			if (localTask == null) {
				// Задача есть на удаленной стороне, но отсутствует локально — добавляем
				tasksToSave.add(new Task(null, owner, sectionName, remoteTask.getOrder(), remoteTask.getContent(), remoteTask.isCompleted()));
			} else {
				// Задача есть и там, и там — обновляем, если необходимо
				if (!localTask.getSection().equals(sectionName) || localTask.getOrder() != remoteTask.getOrder() || localTask.isCompleted() != remoteTask.isCompleted()) {
					localTask.setSection(sectionName);
					localTask.setOrder(remoteTask.getOrder());
					localTask.setCompleted(remoteTask.isCompleted());
					tasksToSave.add(localTask);
				}
				// Удаляем задачу из мапы существующих задач, чтобы позже удалить те, которые не были обработаны
				existTasksMap.remove(remoteTask.getContent());
			}
		}


		taskRepository.deleteAll(existTasksMap.values());
		taskRepository.saveAll(tasksToSave);
	}

}
