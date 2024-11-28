package org.baylist.todoist.api;

import org.baylist.todoist.dto.Project;

import java.util.List;

public class TodoistImpl implements Todoist {

    @Override
    public List<Project> getProjects() {
        return List.of();
    }

    @Override
    public Project getProject(long index) {
        return null;
    }
}
