package org.baylist.db;

import lombok.Data;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Task;

import java.util.List;

@Data
public class ProjectDb {

    private Project project;
    private List<Task> tasks;
    private List<SectionDb> sections;

    @Override
    public String toString() {
        List<Task> tasksWithoutSections = tasks
                .stream()
                .filter(t -> sections.stream().noneMatch(s -> s.getTasks().contains(t)))
                .toList();


        StringBuilder sb = new StringBuilder();
        sb.append("проект - ").append(project.getName()).append("\n\n");
        sb.append("  задачи:\n\n");
        tasksWithoutSections.forEach(t -> sb.append("    - ").append(t.getContent()).append("\n"));
        sections.forEach(s -> {
            sb.append("\n").append(" ↘").append(s.getSection().getName()).append("\n\n");
            s.getTasks().forEach(t -> sb.append("    - ").append(t.getContent()).append("\n"));
        });

        return sb.toString();
    }


}
