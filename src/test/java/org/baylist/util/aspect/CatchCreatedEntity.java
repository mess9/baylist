package org.baylist.util.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.baylist.todoist.dto.Project;
import org.baylist.todoist.dto.Section;
import org.baylist.todoist.dto.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Aspect
public class CatchCreatedEntity {

    public static final List<Project> projects = new CopyOnWriteArrayList<>();
    public static final List<Section> sections = new CopyOnWriteArrayList<>();
    public static final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Pointcut("execution(public * org.baylist.todoist.controller.TodoistController.createProject(..))")
    public void createProject() {
    }

    @AfterReturning(pointcut = "createProject()", returning = "result")
    public void catchProject(Object result) {
        if (result instanceof Project catchProject) {
            projects.add(catchProject);
        }
    }

    @Pointcut("execution(public * org.baylist.todoist.controller.TodoistController.createSection(..))")
    public void createSection() {
    }

    @AfterReturning(pointcut = "createSection()", returning = "result")
    public void catchSection(Object result) {
        if (result instanceof Section catchSection) {
            sections.add(catchSection);
        }
    }

    @Pointcut("execution(public * org.baylist.todoist.controller.TodoistController.createTask(..))")
    public void createTask() {
    }

    @AfterReturning(pointcut = "createTask()", returning = "result")
    public void catchTask(Object result) {
        if (result instanceof Task catchTask) {
            tasks.add(catchTask);
        }
    }
}
