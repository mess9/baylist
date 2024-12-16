package org.baylist.util.extension;

import org.baylist.controller.todoist.TodoistController;
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
        TodoistController bean = GetStatic.getBean(TodoistController.class);
        if (!projects.isEmpty()) {
            projects.forEach(e -> bean.deleteProject(Long.parseLong(e.getId())));
        }
        if (!sections.isEmpty()) {
            sections.forEach(e -> bean.deleteSection(Long.parseLong(e.getId())));
        }
        if (!tasks.isEmpty()) {
            tasks.forEach(e -> bean.deleteTask(Long.parseLong(e.getId())));
        }
    }
}
