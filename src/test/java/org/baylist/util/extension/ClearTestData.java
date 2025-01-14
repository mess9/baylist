package org.baylist.util.extension;

import org.baylist.api.TodoistFeignClient;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.baylist.util.config.GetStatic;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.stereotype.Component;

import static org.baylist.util.aspect.CatchCreatedEntity.projects;
import static org.baylist.util.aspect.CatchCreatedEntity.sections;
import static org.baylist.util.aspect.CatchCreatedEntity.tasks;

@Component
public class ClearTestData implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        TodoistFeignClient todoistApi = GetStatic.getBean(TodoistFeignClient.class);
        UserService userService = GetStatic.getBean(UserService.class);
        User fil = userService.getFil();
        String token = "Bearer " + fil.getTodoistToken();
        if (!projects.isEmpty()) {
            projects.forEach(e -> todoistApi.deleteProject(token, e.getId()));
        }
        if (!sections.isEmpty()) {
            sections.forEach(e -> todoistApi.deleteSection(token, e.getId()));
        }
        if (!tasks.isEmpty()) {
            tasks.forEach(e -> todoistApi.deleteTask(token, e.getId()));
        }
    }
}
