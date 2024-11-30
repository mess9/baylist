package org.baylist.todoist.controller;

import lombok.AllArgsConstructor;
import org.baylist.todoist.api.Todoist;
import org.baylist.todoist.dto.Label;
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
    private static final String LABEL_METHOD = "labels";

    private static final String PROJECT_ID_PARAM = "project_id";
    private static final String SECTION_ID_PARAM = "section_id";
    private static final String LABEL_PARAM = "label";

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
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(TASK_METHOD)
                        .queryParam(PROJECT_ID_PARAM, index)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Task> getTasksBySection(long index) {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(TASK_METHOD)
                        .queryParam(SECTION_ID_PARAM, index)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Task> getTasksByLabel(String label) {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(TASK_METHOD)
                        .queryParam(LABEL_PARAM, label)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Section> getSections() {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(SECTION_METHOD)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Section> getSectionsByProject(long index) {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(SECTION_METHOD)
                        .queryParam(PROJECT_ID_PARAM, index)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<Label> getLabels() {
        return restClient
                .get()
                .uri(UriComponentsBuilder
                        .fromPath("")
                        .pathSegment(LABEL_METHOD)
                        .build()
                        .toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
