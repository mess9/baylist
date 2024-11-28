package org.baylist.todoist.controller;

import lombok.AllArgsConstructor;
import org.baylist.todoist.api.Todoist;
import org.baylist.todoist.dto.Project;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@AllArgsConstructor
public class TodoistController implements Todoist {

    private final RestClient restClient;

    public List<Project> getProjects() {
        return restClient
                .get()
                .uri("/projects")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public Project getProject(long index) {
        return restClient
                .get()
                .uri("projects/" + index)
                .retrieve()
                .body(Project.class);
    }

}
