package org.baylist.todoist.api;

import org.baylist.todoist.dto.Project;

import java.util.List;

public interface Todoist {

    List<Project> getProjects();

    Project getProject(int index);

}
