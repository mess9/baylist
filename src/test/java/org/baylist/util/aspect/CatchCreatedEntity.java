package org.baylist.util.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("EmptyMethod")
@Component
@Aspect
public class CatchCreatedEntity {

    public static final List<Project> projects = new CopyOnWriteArrayList<>();
    public static final List<Section> sections = new CopyOnWriteArrayList<>();
	public static final List<TaskResponse> tasks = new CopyOnWriteArrayList<>();

    @Pointcut("execution(public * org.baylist.service.TodoistService.createProject(..))")
    public void createProjectFeign() {
    }

    @AfterReturning(pointcut = "createProjectFeign()", returning = "result")
    public void catchProjectFeign(Object result) {
        if (result instanceof Project catchProject) {
            projects.add(catchProject);
        }
    }

    @Pointcut("execution(public * org.baylist.service.TodoistService.createSection(..))")
    public void createSectionFeign() {
    }

    @AfterReturning(pointcut = "createSectionFeign()", returning = "result")
    public void catchSectionFeign(Object result) {
        if (result instanceof Section catchSection) {
            sections.add(catchSection);
        }
    }

    @Pointcut("execution(public * org.baylist.service.TodoistService.createTask(..))")
    public void createTaskFeign() {
    }

    @AfterReturning(pointcut = "createTaskFeign()", returning = "result")
    public void catchTaskFeign(Object result) {
	    if (result instanceof TaskResponse catchTask) {
            tasks.add(catchTask);
        }
    }
}
