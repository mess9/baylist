package org.baylist.todoist.service;

import lombok.AllArgsConstructor;
import org.baylist.config.Storage;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoistService {
    private final TodoistController todoistController;

    public void fillProjectMap() {
        Storage.projects.putAll(todoistController.getProjects()
                .stream()
                .collect(Collectors.toMap(k -> Long.parseLong(k.getId()), Function.identity())));
    }

    public Project getProjectById(long id) {
        return todoistController.getProject(id);
    }
}
