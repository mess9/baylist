package org.baylist.tests;

import org.baylist.controller.todoist.Todoist;
import org.baylist.controller.todoist.TodoistControllerOnRestTemplate;
import org.baylist.dto.todoist.api.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoistControllerRestTest extends BaseTest {
    @Autowired
    Todoist restTemplate;
    @Test
    void getAllProjects() {
        List<Project> projects = restTemplate.getProjects();

        assertThat(projects)
                .isNotEmpty()
                .hasSizeGreaterThan(1);
    }

}
