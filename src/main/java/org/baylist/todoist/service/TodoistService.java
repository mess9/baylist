package org.baylist.todoist.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.ProjectDb;
import org.baylist.db.Repository;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class TodoistService {

    private final TodoistController todoistController;
    private final Repository repository;


    public void syncData() {
        log.info("request to todoist for get data");
        List<Project> projects = todoistController.getProjects();
        List<Section> sections = todoistController.getSections();
        List<Task> tasks = todoistController.getTasks();

        repository.fillStorage(projects, sections, tasks);
    }

    public String getProjectByName(String name) {
        Optional<ProjectDb> projectByName = repository.getProjectByName(name.toLowerCase());
        if (projectByName.isPresent()) {
            return projectByName.get().toString();
        } else {
            return "Project not found";
        }
    }

    public String getProjects() {
        List<ProjectDb> projects = repository.getProjects();
        List<String> projectsNames = projects
                .stream()
                .map(p -> p.getProject().getName().toLowerCase())
                .toList();
        StringBuilder sb = new StringBuilder();
        sb.append("Есть такие проекты: \n");
        projectsNames.forEach(m -> sb.append(" - ").append(m).append("\n"));
        return sb.toString();
    }

    public String getProjectsName() {
        return repository.getProjects().stream().map(p -> p.getProject().getName()).reduce((s1, s2) -> s1 + s2)
                .orElseThrow().toLowerCase();
    }

    public boolean storageIsEmpty() {
        return repository.isEmpty();
    }


}
