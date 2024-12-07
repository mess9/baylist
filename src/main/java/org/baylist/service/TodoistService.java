package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.ProjectDb;
import org.baylist.db.Repository;
import org.baylist.db.SectionDb;
import org.baylist.dto.todoist.Project;
import org.baylist.dto.todoist.Section;
import org.baylist.dto.todoist.Task;
import org.baylist.todoist.api.Todoist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;


@Service
@AllArgsConstructor
@Slf4j
public class TodoistService {

    private final Todoist todoistController;
    private final DictionaryService dictionaryService;
    private final Repository repository;


    public void syncData() {
        log.info("request to todoist for get data");
        List<Project> projects = todoistController.getProjects();
        List<Section> sections = todoistController.getSections();
        List<Task> tasks = todoistController.getTasks();

        repository.fillStorage(projects, sections, tasks);
    }

    public String getProjectByName(String name) {
        Optional<ProjectDb> projectByName = repository.getProjectByName(name.toLowerCase());
        if (projectByName.isPresent()) {
            return projectByName.get().toString();
        } else {
            return "Project not found";
        }
    }

    public String getProjects() {
        List<ProjectDb> projects = repository.getProjects();
        List<String> projectsNames = projects
                .stream()
                .map(p -> p.getProject().getName().toLowerCase())
                .toList();
        StringBuilder sb = new StringBuilder();
        sb.append("Есть такие проекты: \n");
        projectsNames.forEach(m -> sb.append(" - ").append(m).append("\n"));
        return sb.toString();
    }

    public String sendTaskToTodoist(String input) {
        if (validateInput(input)) {
            Map<String, Set<String>> inputMap = dictionaryService.parseInputBuyList(input);
            List<SectionDb> sectionsTodoist = repository.getSections();
            Optional<String> buyListProjectId = repository.getBuyListProjectId();

            if (buyListProjectId.isPresent()) {
                sendTasksToProject(inputMap, buyListProjectId.get(), sectionsTodoist);
                return "список покупок был отправлен филу";
            } else {
                Project buylist = todoistController.createProject(Project.builder().setName("buylist").build());
                sendTasksToProject(inputMap, buylist.getId(), sectionsTodoist);
                return "список покупок был филу отправлен";
            }
        } else {
            return "что-то коряво написано, не могу разобрать";
        }
    }

    public String clearBuyList() {
        Optional<ProjectDb> buyListProject = repository.getBuyListProject();
        if (buyListProject.isPresent()) {
            ProjectDb project = buyListProject.get();
            project.getTasks().forEach(t -> todoistController.deleteTask(Long.parseLong(t.getId())));
            return "список покупок очищен";
        } else {
            return "проекта со списком покупок не существует";
        }
    }


    public boolean storageIsEmpty() {
        return repository.isEmpty();
    }


    private void sendTasksToProject(Map<String, Set<String>> inputMap, String buyListProjectId, List<SectionDb> sectionsTodoist) {
        inputMap.forEach((sectionInput, taskList) -> {
            if (sectionInput.equals(UNKNOWN_CATEGORY)) {
                sendTaskWithoutSection(taskList, buyListProjectId);
            } else {
                sectionsTodoist
                        .stream()
                        .map(SectionDb::getSection)
                        .forEach(sectionTodoist -> {
                            if (sectionTodoist.getName().equals(sectionInput)) {
                                sendTasksBySection(
                                        taskList,
                                        sectionTodoist,
                                        buyListProjectId);
                            } else {
                                sendTasksBySection(
                                        taskList,
                                        sendSection(sectionInput, buyListProjectId),
                                        buyListProjectId);
                            }
                        });
            }
        });
    }

    private boolean validateInput(String input) {
        return !input.isEmpty(); //todo написать нормальную валидацию, но надо придумать примеров
    }

    private void sendTaskWithoutSection(Set<String> taskList, String buyListProjectId) {
        taskList.forEach(t -> todoistController.createTask(Task.builder()
                .xyiContent(t)
                .xyiProjectId(buyListProjectId)
                .build()));
    }

    private void sendTasksBySection(Set<String> taskList, Section section, String buyListProjectId) {
        taskList.forEach(t -> todoistController.createTask(Task.builder()
                .xyiContent(t)
                .xyiSectionId(section.getId())
                .xyiProjectId(buyListProjectId)
                .build()));
    }

    private Section sendSection(String sectionInput, String buyListProjectId) {
        return todoistController.createSection(Section.builder()
                .setName(sectionInput)
                .setProjectId(buyListProjectId)
                .build());
    }


}
