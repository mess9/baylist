package org.baylist.controller.todoist;

import lombok.AllArgsConstructor;
import org.baylist.dto.todoist.api.Label;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.baylist.exception.TodoistApiException;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.baylist.util.convert.ToJson.toJson;

@Primary
@Controller
@AllArgsConstructor
public class TodoistControllerOnRestTemplate implements Todoist {

    //todo механизм ретраев неудачных запросов
    private static final String PROJECT_METHOD = "projects";
    private static final String TASK_METHOD = "tasks";
    private static final String SECTION_METHOD = "sections";
    private static final String LABEL_METHOD = "labels";

    private static final String PROJECT_ID_PARAM = "project_id";
    private static final String SECTION_ID_PARAM = "section_id";
    private static final String LABEL_PARAM = "label";

    private final RestTemplate restTemplate;

    //region GET

    @Override
    public List<Project> getProjects() {
        String url = UriComponentsBuilder.fromPath(PROJECT_METHOD).build().toUriString();

        ResponseEntity<List<Project>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return responseEntity.getBody();
    }

    @Override
    public Project getProject(Long index) {
        String url = UriComponentsBuilder.fromPath(PROJECT_METHOD)
                .pathSegment(String.valueOf(index))
                .build()
                .toUriString();

        ResponseEntity<Project> responseEntity = restTemplate.getForEntity(url, Project.class);

        return responseEntity.getBody();
    }

    @Override
    public List<Task> getTasks() {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD).build().toUriString();

        ResponseEntity<List<Task>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Task> getTasksByProject(Long index) {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD)
                .queryParam(PROJECT_ID_PARAM, index)
                .build()
                .toUriString();

        ResponseEntity<List<Task>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Task> getTasksBySection(Long index) {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD)
                .queryParam(SECTION_ID_PARAM, index)
                .build()
                .toUriString();

        ResponseEntity<List<Task>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Task> getTasksByLabel(String label) {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD)
                .queryParam(LABEL_PARAM, label)
                .build()
                .toUriString();

        ResponseEntity<List<Task>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Section> getSections() {
        String url = UriComponentsBuilder.fromPath(SECTION_METHOD).build().toUriString();

        ResponseEntity<List<Section>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Section> getSectionsByProject(Long index) {
        String url = UriComponentsBuilder.fromPath(SECTION_METHOD)
                .queryParam(PROJECT_ID_PARAM, index)
                .build()
                .toUriString();

        ResponseEntity<List<Section>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    @Override
    public List<Label> getLabels() {
        String url = UriComponentsBuilder.fromPath(LABEL_METHOD).build().toUriString();

        ResponseEntity<List<Label>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    //endregion GET

    //region CREATE

    @Override
    public Project createProject(Project project) {
        String url = UriComponentsBuilder.fromPath(PROJECT_METHOD).build().toUriString();

        HttpEntity<Project> requestEntity = new HttpEntity<>(project);
        ResponseEntity<Project> responseEntity = restTemplate.postForEntity(url, requestEntity, Project.class);

        return responseEntity.getBody();
    }

    @Override
    public Section createSection(Section section) {
        String url = UriComponentsBuilder.fromPath(SECTION_METHOD).build().toUriString();

        HttpEntity<Section> requestEntity = new HttpEntity<>(section);
        ResponseEntity<Section> responseEntity = restTemplate.postForEntity(url, requestEntity, Section.class);

        return responseEntity.getBody();
    }

    @Override
    public Task createTask(Task task) {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD).build().toUriString();

        HttpEntity<Task> requestEntity = new HttpEntity<>(task);
        ResponseEntity<Task> responseEntity = restTemplate.postForEntity(url, requestEntity, Task.class);

        return responseEntity.getBody();
    }

    //endregion CREATE

    //region DELETE

    @Override
    public void deleteProject(Long projectId) {
        String url = UriComponentsBuilder.fromPath(PROJECT_METHOD)
                .pathSegment(String.valueOf(projectId))
                .build()
                .toUriString();

        restTemplate.delete(url);
    }

    @Override
    public void deleteSection(Long sectionId) {
        String url = UriComponentsBuilder.fromPath(SECTION_METHOD)
                .pathSegment(String.valueOf(sectionId))
                .build()
                .toUriString();

        restTemplate.delete(url);
    }

    @Override
    public void deleteTask(Long taskId) {
        String url = UriComponentsBuilder.fromPath(TASK_METHOD)
                .pathSegment(String.valueOf(taskId))
                .build()
                .toUriString();

        restTemplate.delete(url);
    }


//endregion DELETE

//region UPDATE

    @Override
    public void updateProject(Project project) {
        String url = UriComponentsBuilder.fromPath("")
                .pathSegment(PROJECT_METHOD)
                .pathSegment(String.valueOf(project.getId()))
                .build().toUriString();

        HttpEntity<String> requestEntity = new HttpEntity<>(toJson(project));

        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    @Override
    public void updateSection(Section section) {
        String url = UriComponentsBuilder.fromPath("")
                .pathSegment(SECTION_METHOD)
                .pathSegment(String.valueOf(section.getId()))
                .build().toUriString();

        HttpEntity<String> requestEntity = new HttpEntity<>(toJson(section));

        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

    }

    @Override
    public void updateTask(Task task) {
        String url = UriComponentsBuilder.fromPath("")
                .pathSegment(TASK_METHOD)
                .pathSegment(String.valueOf(task.getId()))
                .build().toUriString();

        HttpEntity<String> requestEntity = new HttpEntity<>(toJson(task));

        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    //endregion UPDATE

    @Override
    public void closeTask(Long taskId) {
        String url = UriComponentsBuilder.fromPath("")
                .pathSegment(TASK_METHOD)
                .pathSegment(String.valueOf(taskId))
                .pathSegment("close")
                .build().toUriString();

        restTemplate.postForLocation(url, null);
    }

    @Override
    public void reopenTask(Long taskId) {
        String url = UriComponentsBuilder.fromPath("")
                .pathSegment(TASK_METHOD)
                .pathSegment(String.valueOf(taskId))
                .pathSegment("reopen")
                .build().toUriString();

        restTemplate.postForLocation(url, null);
    }
}
