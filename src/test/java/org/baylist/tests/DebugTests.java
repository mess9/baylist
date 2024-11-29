package org.baylist.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DebugTests {

    @Autowired
    TodoistController todoistController;

    @Test
    public void getAllProjects() {
        List<Project> projects = todoistController.getProjects();

        assertThat(projects)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getProjectById() {
        List<Project> projects = todoistController.getProjects();
        Project project = todoistController.getProject(Long.parseLong(projects.getFirst().getId()));

        assertThat(project).isNotNull();
    }

    @Test
    public void getAllOpenTasks() {
        List<Task> tasks = todoistController.getTasks();

        assertThat(tasks)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

    @Test
    public void debug() {
        String body = "[\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593410249\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736069\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"армянский\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"retry\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:50:37.658116Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593410249\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593410309\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736069\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"английский\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"retry\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:50:40.513423Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593410309\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593410577\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736054\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 0,\n" +
                "\t\t\"content\": \"зубной камень\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"outside\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:50:51.202490Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593410577\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593410767\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736054\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"зубы лу\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"outside\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:50:57.329334Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593410767\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593414320\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736054\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"проверить жкт\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"outside\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:51:07.813294Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593414320\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593415082\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736052\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 0,\n" +
                "\t\t\"content\": \"записаться в автошколу\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:51:41.211130Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593415082\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593415143\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736052\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"получить права\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:51:43.205147Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593415143\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593415218\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736052\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"купить машину\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:51:45.530629Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593415218\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593415276\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736052\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 3,\n" +
                "\t\t\"content\": \"купить гараж\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:51:47.485975Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593415276\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593416363\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"разобрать вещи по местам\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:13.094121Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593416363\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593416692\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"сделать полочки\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"buy\",\n" +
                "\t\t\t\"outside\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:24.593157Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593416692\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593416901\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 3,\n" +
                "\t\t\"content\": \"сделать освещение на лентах\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"buy\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:30.584811Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593416901\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593417413\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 4,\n" +
                "\t\t\"content\": \"разобраться с питанием\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:44.447115Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593417413\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593417588\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 5,\n" +
                "\t\t\"content\": \"заказать на али всё необходимое\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:50.909136Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593417588\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593417763\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 6,\n" +
                "\t\t\"content\": \"пересмотреть алекс гайвера\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:52:57.620795Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593417763\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593417985\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 7,\n" +
                "\t\t\"content\": \"степлер\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:53:07.262865Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593417985\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593418058\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 8,\n" +
                "\t\t\"content\": \"провода\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:53:09.046262Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593418058\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593418910\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416901\",\n" +
                "\t\t\"order\": 9,\n" +
                "\t\t\"content\": \"тумблеры или тач включение\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:53:33.797145Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593418910\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593421392\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 4,\n" +
                "\t\t\"content\": \"купить фанеру\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:55:06.081625Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593421392\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593421740\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 5,\n" +
                "\t\t\"content\": \"купить струбцины\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:55:20.759280Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593421740\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593422028\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 6,\n" +
                "\t\t\"content\": \"купить уголков\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:55:29.926840Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593422028\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593422188\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 7,\n" +
                "\t\t\"content\": \"купить дюбели\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:55:34.913107Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593422188\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593422994\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 8,\n" +
                "\t\t\"content\": \"морилка\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:56:00.819561Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593422994\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593423423\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": \"173736040\",\n" +
                "\t\t\"parent_id\": \"8593416692\",\n" +
                "\t\t\"order\": 9,\n" +
                "\t\t\"content\": \"лак/воск/масло\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:56:17.522522Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593423423\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593426826\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592285\",\n" +
                "\t\t\"section_id\": null,\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"починить зелёную куртку\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"outside\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:57:40.471859Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593426826\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593428985\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592642\",\n" +
                "\t\t\"section_id\": \"173736574\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"устроится на работу\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:58:51.847779Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593428985\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593429551\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592642\",\n" +
                "\t\t\"section_id\": \"173736583\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"теория тестирования\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:59:13.349680Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593429551\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593430567\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592642\",\n" +
                "\t\t\"section_id\": \"173736591\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"сформулировать требования\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T12:59:42.026149Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593430567\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593434671\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736745\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"фоллаут эквестрия\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:00:56.199225Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593434671\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593434743\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736745\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"про наёмников\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:00:59.051577Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593434743\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593435276\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736766\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"хеви трип\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:01:17.574686Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593435276\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593435434\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736766\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"про немцев\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:01:22.425036Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593435434\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593436212\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736821\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"последняя часть dishonored\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:01:50.478783Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593436212\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593437297\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592712\",\n" +
                "\t\t\"section_id\": \"173736867\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"сходить на лалвар\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:02:29.674228Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593437297\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593451649\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592642\",\n" +
                "\t\t\"section_id\": \"173736583\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"qa guru\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:09:04.730812Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593451649\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8593452101\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343592642\",\n" +
                "\t\t\"section_id\": \"173736583\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 3,\n" +
                "\t\t\"content\": \"spring ?\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-16T13:09:17.954777Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8593452101\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8615124639\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2172143779\",\n" +
                "\t\t\"section_id\": null,\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 1,\n" +
                "\t\t\"content\": \"задача\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [\n" +
                "\t\t\t\"buy\"\n" +
                "\t\t],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-23T11:15:22.442264Z\",\n" +
                "\t\t\"due\": {\n" +
                "\t\t\t\"date\": \"2024-11-23\",\n" +
                "\t\t\t\"string\": \"23 ноября 19:00\",\n" +
                "\t\t\t\"lang\": \"ru\",\n" +
                "\t\t\t\"is_recurring\": false,\n" +
                "\t\t\t\"datetime\": \"2024-11-23T19:00:00\"\n" +
                "\t\t},\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8615124639\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8615134479\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343982300\",\n" +
                "\t\t\"section_id\": \"174418734\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 3,\n" +
                "\t\t\"content\": \"лова\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-23T11:21:31.213451Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8615134479\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"8615134534\",\n" +
                "\t\t\"assigner_id\": null,\n" +
                "\t\t\"assignee_id\": null,\n" +
                "\t\t\"project_id\": \"2343982300\",\n" +
                "\t\t\"section_id\": \"174418734\",\n" +
                "\t\t\"parent_id\": null,\n" +
                "\t\t\"order\": 2,\n" +
                "\t\t\"content\": \"вап\",\n" +
                "\t\t\"description\": \"\",\n" +
                "\t\t\"is_completed\": false,\n" +
                "\t\t\"labels\": [],\n" +
                "\t\t\"priority\": 1,\n" +
                "\t\t\"comment_count\": 0,\n" +
                "\t\t\"creator_id\": \"15513607\",\n" +
                "\t\t\"created_at\": \"2024-11-23T11:21:33.116820Z\",\n" +
                "\t\t\"due\": null,\n" +
                "\t\t\"url\": \"https://app.todoist.com/app/task/8615134534\",\n" +
                "\t\t\"duration\": null,\n" +
                "\t\t\"deadline\": null\n" +
                "\t}\n" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        List<Task> tasks;
        try {
            tasks = mapper.readValue(body, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(tasks);
    }


}
