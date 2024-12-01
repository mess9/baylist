package org.baylist.config;

import org.baylist.todoist.dto.Label;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    public static final Map<Long, Project> projects = new HashMap<>();
    public static final Map<Long, Task> allTasks = new HashMap<>();
    public static final Map<Long, Task> tasksByProject = new HashMap<>();
    public static final Map<Long, Task> tasksBySection = new HashMap<>();
    public static final Map<Long, Task> tasksByLabel = new HashMap<>();
    public static final Map<Long, Section> sections = new HashMap<>();
    public static final Map<Long, Section> sectionsByProject = new HashMap<>();
    public static final Map<Long, Label> labels = new HashMap<>();

}
