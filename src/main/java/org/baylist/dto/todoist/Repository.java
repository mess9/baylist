package org.baylist.dto.todoist;

import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.baylist.dto.Constants.BUYLIST_PROJECT;

@Component
public class Repository {

    private final Storage storage = new Storage();

    public void fillStorage(List<Project> projects,
                            List<Section> sections,
                            List<Task> tasks) {

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
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public Optional<ProjectDb> getProjectByName(String name) {
        return storage.getProjects()
                .stream()
                .filter(p -> p.getProject().getName().equalsIgnoreCase(name))
                .findAny();
    }

    public List<SectionDb> getSections() {
        return storage.getProjects()
                .stream().filter(p -> p.getProject().getName().equals(BUYLIST_PROJECT))
                .map(ProjectDb::getSections)
                .findAny()
                .orElse(new ArrayList<>());
    }

    public Optional<ProjectDb> getBuyListProject() {
        return storage.getProjects()
                .stream()
                .filter(project -> project.getProject().getName().equals(BUYLIST_PROJECT))
                .findAny();
    }


}
