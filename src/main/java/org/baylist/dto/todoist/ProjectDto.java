package org.baylist.dto.todoist;

import lombok.Data;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Task;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ProjectDto {

	private Project project;
	private List<Task> tasks;
	private List<SectionDto> sections;


	@Override
	public String toString() {
		List<Task> tasksWithoutSections = getTasksWithoutSection();
		StringBuilder sb = new StringBuilder();
		sb.append("<b>").append(project.getName()).append("</b>").append("\n");
		AtomicInteger counter = new AtomicInteger();
		if (!tasksWithoutSections.isEmpty()) {
			sb.append("  <code>внекатегорийные задачи:</code>").append("\n");
			tasksWithoutSections.forEach(t ->
					sb.append("    - ").append("<i>").append(t.getContent()).append("</i>").append("\n"));
			counter.getAndIncrement();
		}

		sections.stream()
				.filter(s -> !s.getTasks().isEmpty())
				.forEach(s -> {
					sb.append("\n").append(" ↘").append("<code>").append(s.getSection().getName()).append("</code>").append("\n");
					s.getTasks().forEach(t -> sb.append("      - ").append("<i>").append(t.getContent()).append("</i>").append("\n"));
					counter.getAndIncrement();
				});

		if (counter.get() == 0) {
			sb.append("открытых задач нет\n").append("абсолютно. ничего. тут. нет.");
		}

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
