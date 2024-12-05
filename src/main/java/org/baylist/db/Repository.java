package org.baylist.db;

import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    public List<ProjectDb> getProjects() {
        return storage.getProjects();
    }

    public Optional<ProjectDb> getProjectByName(String name) {
        return storage.getProjects()
                .stream()
                .filter(p -> p.getProject().getName().equalsIgnoreCase(name))
                .findAny();
    }

    //todo нужен метод diff - для проверки изменений на той или иной стороне

}
