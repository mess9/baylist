package org.baylist.util.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Aspect
public class CatchCreatedEntity {

    public static final List<Project> projects = new CopyOnWriteArrayList<>();
    public static final List<Section> sections = new CopyOnWriteArrayList<>();
    public static final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Pointcut("execution(public * org.baylist.controller.todoist.TodoistController.createProject(..))")
    public void createProject() {
    }

    @Pointcut("execution(public * org.baylist.service.TodoistService.createProject(..))")
    public void createProjectFeign() {
    }

    @AfterReturning(pointcut = "createProject()", returning = "result")
    public void catchProject(Object result) {
        if (result instanceof Project catchProject) {
            projects.add(catchProject);
        }
    }

    @AfterReturning(pointcut = "createProjectFeign()", returning = "result")
    public void catchProjectFeign(Object result) {
        if (result instanceof Project catchProject) {
            projects.add(catchProject);
        }
    }

    @Pointcut("execution(public * org.baylist.controller.todoist.TodoistController.createSection(..))")
    public void createSection() {
    }

    @Pointcut("execution(public * org.baylist.service.TodoistService.createSection(..))")
    public void createSectionFeign() {
    }

    @AfterReturning(pointcut = "createSection()", returning = "result")
    public void catchSection(Object result) {
        if (result instanceof Section catchSection) {
            sections.add(catchSection);
        }
    }

    @AfterReturning(pointcut = "createSectionFeign()", returning = "result")
    public void catchSectionFeign(Object result) {
        if (result instanceof Section catchSection) {
            sections.add(catchSection);
        }
    }

    @Pointcut("execution(public * org.baylist.controller.todoist.TodoistController.createTask(..))")
    public void createTask() {
    }

    @Pointcut("execution(public * org.baylist.service.TodoistService.createTask(..))")
    public void createTaskFeign() {
    }

    @AfterReturning(pointcut = "createTask()", returning = "result")
    public void catchTask(Object result) {
        if (result instanceof Task catchTask) {
            tasks.add(catchTask);
        }
    }

    @AfterReturning(pointcut = "createTaskFeign()", returning = "result")
    public void catchTaskFeign(Object result) {
        if (result instanceof Task catchTask) {
            tasks.add(catchTask);
        }
    }
}
