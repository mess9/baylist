package org.baylist.db;

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
    public String toString() { //todo прикрутить красивый markdown вывод
        List<Task> tasksWithoutSections = getTasksWithoutSection();
        StringBuilder sb = new StringBuilder();
        sb.append(project.getName()).append("\n");
        sb.append("  задачи не нашедшие категорий:").append("\n");
        tasksWithoutSections.forEach(t -> sb.append("    - ").append(t.getContent()).append("\n"));
        sections.forEach(s -> {
            sb.append("\n").append(" ↘").append(s.getSection().getName()).append("\n");
            s.getTasks().forEach(t -> sb.append("      - ").append(t.getContent()).append("\n"));
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
