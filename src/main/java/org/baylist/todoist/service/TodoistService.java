package org.baylist.todoist.service;

import lombok.AllArgsConstructor;
import org.baylist.db.ProjectDb;
import org.baylist.db.SectionDb;
import org.baylist.db.Storage;
import org.baylist.todoist.controller.TodoistController;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TodoistService {
    private final TodoistController todoistController;

    public Storage syncDate() {
        List<Project> projects = todoistController.getProjects();
        List<Section> sections = todoistController.getSections();
        List<Task> tasks = todoistController.getTasks();

        Storage storage = new Storage();
        List<ProjectDb> projectDbs = projects.stream().map(p -> {
                    ProjectDb projectDb = new ProjectDb();
                    projectDb.setProject(p);

                    List<Section> listSection = sections
                            .stream()
                            .filter(s -> s.getProjectId().equals(p.getId()))
                            .toList();
                    List<Task> tasksByProject = tasks
                            .stream()
                            .filter(t -> t.getProjectId().equals(p.getId()))
                            .toList();
                    projectDb.setTasks(tasksByProject);

                    List<SectionDb> list = listSection
                            .stream()
                            .map(s -> {
                                SectionDb sectionDb = new SectionDb();
                                sectionDb.setSection(s);

                                List<Task> tasksBySection = tasks
                                        .stream()
                                        .filter(t -> t.getSectionId() != null && t.getSectionId().equals(s.getId()))
                                        .toList();
                                sectionDb.setTasks(tasksBySection);
                                return sectionDb;
                            })
                            .toList();
                    projectDb.setSections(list);
                    return projectDb;
                })
                .toList();

        storage.setProjects(projectDbs);

        return storage;
    }
}
