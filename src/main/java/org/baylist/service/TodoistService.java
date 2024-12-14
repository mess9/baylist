package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.deprecated.ProjectDb;
import org.baylist.db.deprecated.Repository;
import org.baylist.db.deprecated.SectionDb;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.todoist.Project;
import org.baylist.dto.todoist.Section;
import org.baylist.dto.todoist.Task;
import org.baylist.todoist.api.Todoist;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.BUYLIST_PROJECT;
import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;


@Service
@AllArgsConstructor
@Slf4j
public class TodoistService {

    private final Todoist todoistController;
    private final DictionaryService dictionaryService;
    private final Repository repository;

    public void syncBuyListData() {
        log.info("request to todoist for get data by buylist project");
        List<Project> projects = todoistController.getProjects();
        Optional<Project> buylistProject = projects
                .stream().filter(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))
                .findAny();
        if (buylistProject.isPresent()) {
            Long projectId = Long.valueOf(buylistProject.get().getId());
            List<Section> todoistBuylistSections = todoistController.getSectionsByProject(projectId);
            List<Task> todoistBuylistTasks = todoistController.getTasksByProject(projectId);
            repository.fillStorage(List.of(buylistProject.get()), todoistBuylistSections, todoistBuylistTasks);
        } else {
            log.error("buylist project not found");
        }
    }

    public String getBuylistProject() {
        syncBuyListData();
        Optional<ProjectDb> projectByName = repository.getProjectByName(BUYLIST_PROJECT);
        if (projectByName.isPresent()) {
            return projectByName.get().toString();
        } else {
            return "Project not found";
        }
    }

    public SendMessage sendTaskToTodoist(String input, SendMessage message) {
        if (validateInput(input)) {
            Map<String, Set<String>> inputMap = dictionaryService.parseInputBuyList(input);
            List<SectionDb> sectionsTodoist = repository.getSections();
            Optional<ProjectDb> buyListProject = repository.getBuyListProject();

            if (buyListProject.isPresent()) {
                distinctNoCategoryTasks(inputMap, buyListProject);
                sendTasksToProject(inputMap, buyListProject.get().getProject().getId(), sectionsTodoist);
                syncBuyListData(); //todo возможно стоит придумать какой то флаг терминальной операции(хз вообще) после которого будет автосинк, что бы не пихать его везде
                message.setText("список покупок был отправлен филу\n" + getBuylistProject());
                message.setParseMode("html");
                return message;
            } else {
                Project buylist = todoistController.createProject(Project.builder().setName(BUYLIST_PROJECT).build());
                sendTasksToProject(inputMap, buylist.getId(), sectionsTodoist);
                syncBuyListData();
                message.setText("список покупок был филу отправлен\n" + getBuylistProject());
                return message;
            }
        } else {
            message.setText("что-то коряво написано, не могу разобрать");
            return message;
        }
    }

    public void syncAllData() { //временно не используется, так как по сути боту оно пока не надо
        log.info("request to todoist for get all data");
        List<Project> projects = todoistController.getProjects();
        List<Section> sections = todoistController.getSections();
        List<Task> tasks = todoistController.getTasks();

        repository.fillStorage(projects, sections, tasks);
    }

    public String getProjectByName(String name) {
        syncBuyListData();
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


    private void distinctNoCategoryTasks(Map<String, Set<String>> inputMap, Optional<ProjectDb> buyListProject) {
        buyListProject.ifPresent(p -> inputMap
                .putIfAbsent(UNKNOWN_CATEGORY, inputMap
                        .values()
                        .stream()
                        .peek(t -> p
                                .getTasks()
                                .stream()
                                .map(Task::getContent)
                                .toList()
                                .forEach(t::remove))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
        );
    }

    public String clearBuyList() {
        Optional<ProjectDb> buyListProject = repository.getBuyListProject();
        if (buyListProject.isPresent()) {
            ProjectDb project = buyListProject.get();
            project.getTasks().forEach(t -> todoistController.deleteTask(Long.parseLong(t.getId())));
            syncBuyListData();
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
            }
            if (!sectionInput.equals(UNKNOWN_CATEGORY)) {
                sectionsTodoist
                        .stream()
                        .peek(s -> s.getTasks().stream().map(Task::getContent).toList().forEach(taskList::remove))
                        .map(SectionDb::getSection)
                        .forEach(sectionTodoist -> {
                            if (sectionTodoist.getName().equals(sectionInput)) {
                                sendTasksBySection(
                                        taskList,
                                        sectionTodoist,
                                        buyListProjectId);
                            }
                        }); //todo а если такой секции ещё нет в проекте - её надо бы создать там.
            }
        });
    }

    private boolean validateInput(String input) {
        return input.length() > 3 &&
                Arrays.stream(Commands.values()).noneMatch(c -> input.contains(c.getCommand()));
        //вероятно в будущем тут будет добавлен ещё ряд условий
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
