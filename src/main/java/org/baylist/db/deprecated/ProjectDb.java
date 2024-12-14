package org.baylist.db.deprecated;

import lombok.Data;
import org.baylist.dto.todoist.Project;
import org.baylist.dto.todoist.Task;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class ProjectDb {

    private Project project;
    private List<Task> tasks;
    private List<SectionDb> sections;

    @Override
    public String toString() {
        List<Task> tasksWithoutSections = getTasksWithoutSection();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(project.getName()).append("</b>").append("\n");
        sb.append("  <code>задачи не нашедшие категорий:</code>").append("\n");
        tasksWithoutSections.forEach(t -> sb.append("    - ").append("<i>").append(t.getContent()).append("</i>").append("\n"));
        sections.forEach(s -> {
            sb.append("\n").append(" ↘").append("<code>").append(s.getSection().getName()).append("</code>").append("\n");
            s.getTasks().forEach(t -> sb.append("      - ").append("<i>").append(t.getContent()).append("</i>").append("\n"));
        });

        return sb.toString();
    }

    @NotNull
    public List<Task> getTasksWithoutSection() {
        return tasks
                .stream()
                .filter(t -> sections.stream().noneMatch(s -> s.getTasks().contains(t)))
                .toList();
    }
}
