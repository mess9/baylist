package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.api.TodoistFeignClient;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.todoist.ProjectDb;
import org.baylist.dto.todoist.Repository;
import org.baylist.dto.todoist.SectionDb;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.BUYLIST_PROJECT;
import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TodoistService {

	TodoistFeignClient todoistApi;
	DictionaryService dictionaryService;
	Repository repository;
	UserService userService;

	public Project createProject(String token, Project project) {
		return todoistApi.createProject(token, project);
	}

	public Section createSection(String token, Section section) {
		return todoistApi.createSection(token, section);
	}

	public Task createTask(String token, Task task) {
		return todoistApi.createTask(token, task);
	}


	public void createProject(ChatValue chatValue) {
		if (todoistApi.getProjects(chatValue.getToken()).stream().noneMatch(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))) {
			todoistApi.createProject(chatValue.getToken(), Project.builder()
					.name(BUYLIST_PROJECT)
					.build());
		}
	}

	public boolean storageIsEmpty() {
		return repository.isEmpty();
	}

	public String clearBuyList(ChatValue chatValue) {
		Optional<ProjectDb> buyListProject = repository.getBuyListProject();
		if (buyListProject.isPresent()) {
			ProjectDb project = buyListProject.get();
			project.getTasks().forEach(t -> todoistApi.deleteTask(chatValue.getToken(), t.getId()));
			syncBuyListData(chatValue);
			return "список покупок беспощадно изничтожен";
		} else {
			return "проекта со списком покупок не существует";
		}
	}

	public void syncBuyListData(ChatValue chatValue) {
		log.info("request to todoist for get data by buylist project");
		List<Project> projects = todoistApi.getProjects(chatValue.getToken());
		Optional<Project> buylistProject = projects
				.stream().filter(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))
				.findAny();
		if (buylistProject.isPresent()) {
			String projectId = buylistProject.get().getId();
			List<Section> todoistBuylistSections = todoistApi.getSectionsByProject(chatValue.getToken(), projectId);
			List<Task> todoistBuylistTasks = todoistApi.getTasksByProject(chatValue.getToken(), projectId);
			repository.fillStorage(List.of(buylistProject.get()), todoistBuylistSections, todoistBuylistTasks);
		} else {
			log.error("buylist project not found");
		}
	}

	public String getBuylistProject(ChatValue chatValue) {
		syncBuyListData(chatValue);
		Optional<ProjectDb> projectByName = repository.getProjectByName(BUYLIST_PROJECT);
		if (projectByName.isPresent()) {
			return projectByName.get().toString();
		} else {
			return "Project not found";
		}
	}

	@Transactional
	public List<User> checkRecipients(ChatValue chatValue) {
		User iUser = chatValue.getUser();
		boolean isExistToken = userService.isExistToken(iUser.getUserId());
		List<User> friendMe = userService.getFriendMe(iUser.getUserId());

		if (isExistToken) friendMe.add(iUser);
		return friendMe;
	}


	public void sendTasksToTodoist(ChatValue chatValue, User recipient) {
		SendMessage message = chatValue.getMessage();
		String input = chatValue.getUpdate().getMessage().getText();

		Map<String, Set<String>> inputTasks = dictionaryService.parseInputBuyList(input, recipient.getUserId());
		Optional<ProjectDb> buyListProjectDb = repository.getBuyListProject();

		if (buyListProjectDb.isPresent()) {
			ProjectDb buylistProject = buyListProjectDb.get();
			String projectId = buylistProject.getProject().getId();
			List<Task> tasks = buylistProject.getTasks();
			List<SectionDb> sections = buylistProject.getSections();
			List<String> submittedTasks = new ArrayList<>();

			submittedTasks.addAll(sendTasksWithoutCategory(inputTasks, tasks, projectId, chatValue.getToken()));
			submittedTasks.addAll(sendTasksWithNotExistCategory(inputTasks, sections, projectId, chatValue.getToken()));
			submittedTasks.addAll(sendTasksWithExistCategory(inputTasks, sections, projectId, chatValue.getToken()));

			resultMessage(submittedTasks, message);
		} else {
			message.setText("проект buylist в todoist не существует," +
					" хотите провести первоначальную настройку (кнопки да/нет)");
			//todo для многопользовательского использования вынести создание проекта в первоначальную настройку
		}

	}


	//private
	private Set<String> sendTasksWithoutCategory(Map<String, Set<String>> inputTasks,
	                                             List<Task> tasks,
	                                             String projectId,
	                                             String token) {
		Set<String> unknownSectionInputTask = inputTasks.get(UNKNOWN_CATEGORY);
		if (unknownSectionInputTask != null && !unknownSectionInputTask.isEmpty()) {
			tasks.stream().map(Task::getContent).toList().forEach(unknownSectionInputTask::remove);
			sendTasks(unknownSectionInputTask, projectId, token);
			return unknownSectionInputTask;
		} else {
			return Set.of();
		}
	}

	private Set<String> sendTasksWithNotExistCategory(Map<String, Set<String>> inputTasks,
	                                                  List<SectionDb> sections,
	                                                  String projectId,
	                                                  String token) {
		List<Section> createdSections = getNotExistSections(inputTasks, sections).stream()
				.map(s -> sendSection(s, projectId, token)).toList();
		Set<String> inputTaskSet = new HashSet<>();
		createdSections.forEach(s -> {
			inputTaskSet.addAll(inputTasks.get(s.getName()));
			sendTasks(inputTaskSet, s, projectId, token);
		});
		return inputTaskSet;
	}

	private Set<String> sendTasksWithExistCategory(Map<String, Set<String>> inputTasks,
	                                               List<SectionDb> sections,
	                                               String projectId,
	                                               String token) {
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
				sendTasks(inputTaskSet, section, projectId, token);
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

	private List<String> getNotExistSections(Map<String, Set<String>> inputTasks, List<SectionDb> sections) {
		return inputTasks.keySet().stream()
				.filter(is -> !is.equals(UNKNOWN_CATEGORY))
				.filter(is -> sections.stream().noneMatch(s -> s.getSection().getName().equals(is)))
				.toList();
	}

	private Section sendSection(String sectionInput, String buyListProjectId, String token) {
		return createSection(token, Section.builder()
				.name(sectionInput)
				.projectId(buyListProjectId)
				.build());
	}

	private void sendTasks(Set<String> taskList, Section section, String buyListProjectId, String token) {
		taskList.forEach(t -> createTask(token, Task.builder()
				.content(t)
				.sectionId(section.getId())
				.projectId(buyListProjectId)
				.build()));
	}

	private void sendTasks(Set<String> taskList, String buyListProjectId, String token) {
		taskList.forEach(t -> createTask(token, Task.builder()
				.content(t)
				.projectId(buyListProjectId)
				.build()));
	}


}
