package org.baylist.dto.todoist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;

import java.util.List;
import java.util.Optional;

import static org.baylist.dto.Constants.BUYLIST_PROJECT;

@Getter
@AllArgsConstructor
public class TodoistState {

    private final List<ProjectDto> projects;

    public TodoistState(List<Project> projects,
                        List<Section> sections,
                        List<TaskResponse> tasks) {
        this.projects = fillStorage(projects, sections, tasks);
    }

    private List<ProjectDto> fillStorage(List<Project> projects,
                                         List<Section> sections,
                                         List<TaskResponse> tasks) {
        return projects.stream().map(p -> {
                    ProjectDto projectDto = new ProjectDto();
                    projectDto.setProject(p);

                    List<Section> listSection = sections
                            .stream()
                            .filter(s -> s.getProjectId().equals(p.getId()))
                            .toList();
                    List<TaskResponse> tasksByProject = tasks
                            .stream()
                            .filter(t -> t.getProjectId().equals(p.getId()))
                            .toList();
                    projectDto.setTasks(tasksByProject);

                    List<SectionDto> list = listSection
                            .stream()
                            .map(s -> {
                                SectionDto sectionDto = new SectionDto();
                                sectionDto.setSection(s);

                                List<TaskResponse> tasksBySection = tasks
                                        .stream()
                                        .filter(t -> t.getSectionId() != null && t.getSectionId().equals(s.getId()))
                                        .toList();
                                sectionDto.setTasks(tasksBySection);
                                return sectionDto;
                            })
                            .toList();
                    projectDto.setSections(list);
                    return projectDto;
                })
                .toList();
    }

    public boolean isEmpty() {
        return projects.isEmpty();
    }

    public Optional<ProjectDto> getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProject().getName().equalsIgnoreCase(name))
                .findAny();
    }

    public Optional<ProjectDto> getBuyListProject() {
        return projects.stream()
                .filter(project -> project.getProject().getName().equals(BUYLIST_PROJECT))
                .findAny();
    }


}
