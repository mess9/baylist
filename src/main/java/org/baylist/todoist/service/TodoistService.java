package org.baylist.todoist.service;

import lombok.AllArgsConstructor;
import org.baylist.config.Storage;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
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

    public void fillTasksMap() {
        Storage.allTasks.putAll(todoistController.getTasks()
                .stream()
                .collect(Collectors.toMap(t -> Long.parseLong(t.getId()), Function.identity())));
    }

    public void fillTasksByProjectIdMap(long projectId) {
        Storage.tasksByProject.putAll(todoistController.getTasksByProject(projectId)
                .stream()
                .collect(Collectors.toMap(t -> Long.parseLong(t.getId()), Function.identity())));
    }

    public void fillTasksBySectionIdMap(long sectionId) {
        Storage.tasksBySection.putAll(todoistController.getTasksBySection(sectionId)
                .stream()
                .collect(Collectors.toMap(t -> Long.parseLong(t.getId()), Function.identity())));
    }

    public void fillTasksByLabelMap(String label) {
        Storage.tasksByLabel.putAll(todoistController.getTasksByLabel(label)
                .stream()
                .collect(Collectors.toMap(t -> Long.parseLong(t.getId()), Function.identity())));
    }

    public void fillSectionsMap() {
        Storage.sections.putAll(todoistController.getSections()
                .stream()
                .collect(Collectors.toMap(s -> Long.parseLong(s.getId()), Function.identity())));

    }

    public void fillSectionByProjectIdMap(long projectId) {
        Storage.sectionsByProject.putAll(todoistController.getSectionsByProject(projectId)
                .stream()
                .collect(Collectors.toMap(k-> Long.parseLong(k.getId()), Function.identity())));
    }

    public void fillLabelsMap() {
        Storage.labels.putAll(todoistController.getLabels()
                .stream()
                .collect(Collectors.toMap(l-> Long.parseLong(l.getId()), Function.identity())));
    }
}
