package org.baylist.todoist.controller;

import lombok.AllArgsConstructor;
import org.baylist.todoist.api.Todoist;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
@AllArgsConstructor
public class TodoistController implements Todoist {

    private static final String PROJECT_METHOD = "projects";
    private static final String TASK_METHOD = "tasks";
    private static final String SECTION_METHOD = "sections";


    private final RestClient restClient;

    @Override
    public List<Project> getProjects() {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(PROJECT_METHOD)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public Project getProject(long index) {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(PROJECT_METHOD)
                        .pathSegment(String.valueOf(index))
                        .build()
                        .toUriString())
                .retrieve()
                .body(Project.class);
    }

    @Override
    public List<Task> getTasks() {

        String string = restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(TASK_METHOD)
                        .build()
                        .toUriString())
                .retrieve()
                .toString();
        System.out.println(string);

        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(TASK_METHOD)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Task> getTasksByProject(long index) {
        return List.of();
    }

    @Override
    public List<Task> getTasksBySection(long index) {
        return List.of();
    }

    @Override
    public List<Task> getTasksByLabel(List<String> labels) {
        return List.of();
    }

    @Override
    public List<Task> getTasksByLabel(String labels) {
        return List.of();
    }

    @Override
    public List<Section> getAllSections() {
        return List.of();
    }

    @Override
    public List<Section> getSectionsByProject(long index) {
        return List.of();
    }

}
