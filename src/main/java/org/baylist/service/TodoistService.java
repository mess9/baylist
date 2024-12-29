package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.controller.todoist.Todoist;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.todoist.ProjectDb;
import org.baylist.dto.todoist.Repository;
import org.baylist.dto.todoist.SectionDb;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.baylist.util.log.RestLog;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
	private RestLog restLog;

	public void createProject(ChatValue chatValue) {
		restLog.setAuthToken(chatValue.getUser().getTodoistToken());
		if (todoistController.getProjects().stream().noneMatch(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))) {
			todoistController.createProject(Project.builder()
					.name(BUYLIST_PROJECT)
					.build());
		}
	}

	public boolean storageIsEmpty() {
		return repository.isEmpty();
	}

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

	public String clearBuyList() {
		Optional<ProjectDb> buyListProject = repository.getBuyListProject();
		if (buyListProject.isPresent()) {
			ProjectDb project = buyListProject.get();
			project.getTasks().forEach(t -> todoistController.deleteTask(Long.parseLong(t.getId())));
			syncBuyListData();
			return "список покупок беспощадно изничтожен";
		} else {
			return "проекта со списком покупок не существует";
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

	public void sendTasksToTodoist(ChatValue chatValue) {
		SendMessage message = chatValue.getMessage();
		String input = chatValue.getUpdate().getMessage().getText();

		if (validateInput(input)) {

			Map<String, Set<String>> inputTasks = dictionaryService.parseInputBuyList(input);
			Optional<ProjectDb> buyListProjectDb = repository.getBuyListProject();

			if (buyListProjectDb.isPresent()) {
				ProjectDb buylistProject = buyListProjectDb.get();
				String projectId = buylistProject.getProject().getId();
				List<Task> tasks = buylistProject.getTasks();
				List<SectionDb> sections = repository.getSections();
				List<String> submittedTasks = new ArrayList<>();

				submittedTasks.addAll(sendTasksWithoutCategory(inputTasks, tasks, projectId));
				submittedTasks.addAll(sendTasksWithNotExistCategory(inputTasks, sections, projectId));
				submittedTasks.addAll(sendTasksWithExistCategory(inputTasks, sections, projectId));

				resultMessage(submittedTasks, message);
			} else {
				message.setText("проект buylist в todoist не существует," +
						" хотите провести первоначальную настройку (кнопки да/нет)");
				//todo для многопользовательского использования вынести создание проекта в первоначальную настройку
			}
		} else {
			message.setText("что-то коряво написано, не могу разобрать");
		}
	}


	//private
	private boolean validateInput(String input) {
		return input.length() > 3 &&
				Arrays.stream(Commands.values()).noneMatch(c -> input.contains(c.getCommand()));
		//вероятно в будущем тут будет добавлен ещё ряд условий
	}

	private Set<String> sendTasksWithoutCategory(Map<String, Set<String>> inputTasks,
	                                             List<Task> tasks,
	                                             String projectId) {
		Set<String> unknownSectionInputTask = inputTasks.get(UNKNOWN_CATEGORY);
		if (unknownSectionInputTask != null && !unknownSectionInputTask.isEmpty()) {
			tasks.stream().map(Task::getContent).toList().forEach(unknownSectionInputTask::remove);
			sendTasks(unknownSectionInputTask, projectId);
			return unknownSectionInputTask;
		} else {
			return Set.of();
		}
	}

	private Set<String> sendTasksWithNotExistCategory(Map<String, Set<String>> inputTasks,
	                                                  List<SectionDb> sections,
	                                                  String projectId) {
		List<Section> createdSections = getNotExistSections(inputTasks, sections).stream()
				.map(s -> sendSection(s, projectId)).toList();
		Set<String> inputTaskSet = new HashSet<>();
		createdSections.forEach(s -> {
			inputTaskSet.addAll(inputTasks.get(s.getName()));
			sendTasks(inputTaskSet, s, projectId);
		});
		return inputTaskSet;
	}

	private Set<String> sendTasksWithExistCategory(Map<String, Set<String>> inputTasks,
	                                               List<SectionDb> sections,
	                                               String projectId) {
		Set<String> submittedTasks = new HashSet<>();
		sections.forEach(s -> {
			String sectionName = s.getSection().getName();
			Section section = s.getSection();
			Set<String> inputTaskSet = inputTasks.get(sectionName);

			if (inputTaskSet != null && !inputTaskSet.isEmpty()) {
				if (!s.getTasks().isEmpty()) {
					inputTaskSet.removeAll(s.getTasks().stream()
							.map(Task::getContent)
							.collect(Collectors.toSet()));
				}
				sendTasks(inputTaskSet, section, projectId);
				submittedTasks.addAll(inputTaskSet);
			}
		});
		return submittedTasks;
	}

	private void resultMessage(List<String> submittedTasks, SendMessage message) {
		InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
				.keyboard(List.of(new InlineKeyboardRow(List.of(
						InlineKeyboardButton.builder()
								.text("показать полный список")
								.callbackData(Callbacks.VIEW.getCallbackData())
								.build()))))
				.build();
		StringBuilder sb = new StringBuilder();
		if (!submittedTasks.isEmpty()) {
			sb.append("были добавлены следующие задачи\n");
			submittedTasks.forEach(t -> sb.append(" - ").append(t).append("\n"));
		} else {
			sb.append("задачи не были добавлены, т.к. они уже присутствуют в существующем списке");
		}
		message.setText(sb.toString());
		message.setReplyMarkup(keyboard);
	}

	private void sendTasks(Set<String> taskList, String buyListProjectId) {
		taskList.forEach(t -> todoistController.createTask(Task.builder()
				.content(t)
				.projectId(buyListProjectId)
				.build()));
	}

	private List<String> getNotExistSections(Map<String, Set<String>> inputTasks, List<SectionDb> sections) {
		return inputTasks.keySet().stream()
				.filter(is -> !is.equals(UNKNOWN_CATEGORY))
				.filter(is -> sections.stream().noneMatch(s -> s.getSection().getName().equals(is)))
				.toList();
	}

	private Section sendSection(String sectionInput, String buyListProjectId) {
		return todoistController.createSection(Section.builder()
				.name(sectionInput)
				.projectId(buyListProjectId)
				.build());
	}

	private void sendTasks(Set<String> taskList, Section section, String buyListProjectId) {
		taskList.forEach(t -> todoistController.createTask(Task.builder()
				.content(t)
				.sectionId(section.getId())
				.projectId(buyListProjectId)
				.build()));
	}


}
