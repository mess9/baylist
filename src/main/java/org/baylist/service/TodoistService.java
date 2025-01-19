package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.api.TodoistFeignClient;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.todoist.ProjectDto;
import org.baylist.dto.todoist.SectionDto;
import org.baylist.dto.todoist.TodoistState;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
	Map<Long, TodoistState> todoistStateMap = new ConcurrentHashMap<>();
	UserService userService;
	HistoryService historyService;

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
		String token = chatValue.getToken();
		if (todoistApi.getProjects(token).stream().noneMatch(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))) {
			createProject(token, Project.builder()
					.name(BUYLIST_PROJECT)
					.build());
		}
	}

	public String clearBuyList(ChatValue chatValue) {
		syncBuyListData(chatValue.getUser());
		Optional<ProjectDto> buyListProject = todoistStateMap.get(chatValue.getUserId()).getBuyListProject();
			if (buyListProject.isPresent()) {
				ProjectDto project = buyListProject.get();
				project.getTasks().forEach(t -> todoistApi.deleteTask(chatValue.getToken(), t.getId()));
				syncBuyListData(chatValue.getUser());
				return "список покупок беспощадно изничтожен";
			} else {
				return "проекта со списком покупок не существует, можно настроить тут /start";
			}
	}

	public void syncBuyListData(User recipient) {
		log.info("request to todoist for get data by buylist project");
		List<Project> projects = todoistApi.getProjects(recipient.getBearerToken());
		Optional<Project> buylistProject = projects
				.stream().filter(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))
				.findAny();
		if (buylistProject.isPresent()) {
			String projectId = buylistProject.get().getId();
			List<Section> todoistBuylistSections = todoistApi.getSectionsByProject(recipient.getBearerToken(), projectId);
			List<Task> todoistBuylistTasks = todoistApi.getTasksByProject(recipient.getBearerToken(), projectId);
			todoistStateMap.put(recipient.getUserId(),
					new TodoistState(projects, todoistBuylistSections, todoistBuylistTasks));
		} else {
			log.error("buylist project not found");
		}
	}

	public String getBuylistProject(User recipient) {
		syncBuyListData(recipient);
		Optional<ProjectDto> projectByName = todoistStateMap.get(recipient.getUserId())
				.getProjectByName(BUYLIST_PROJECT);
		if (projectByName.isPresent()) {
			return projectByName.get().toString();
		} else {
			return "Project not found";
		}
	}

	public void sendTasksToTodoist(ChatValue chatValue, User recipient, String input) {
		if (storageIsEmpty(recipient.getUserId())) {
			syncBuyListData(recipient);
		}
		String token = recipient.getBearerToken();

		Map<String, Set<String>> inputTasks = dictionaryService.parseInputWithDict(input, recipient.getUserId());
		Optional<ProjectDto> buyListProjectDb = todoistStateMap.get(recipient.getUserId()).getBuyListProject();

		if (buyListProjectDb.isPresent()) {
			ProjectDto buylistProject = buyListProjectDb.get();
			String projectId = buylistProject.getProject().getId();
			List<Task> tasks = buylistProject.getTasks();
			List<SectionDto> sections = buylistProject.getSections();
			List<String> submittedTasks = new ArrayList<>();

			submittedTasks.addAll(sendTasksWithoutCategory(inputTasks, tasks, projectId, token));
			submittedTasks.addAll(sendTasksWithNotExistCategory(inputTasks, sections, projectId, token));
			submittedTasks.addAll(sendTasksWithExistCategory(inputTasks, sections, projectId, token));

			historyService.sendTasks(chatValue.getUser(), recipient, Action.SEND_TASK, submittedTasks.toString());
			resultMessage(submittedTasks, chatValue);
		} else {
			chatValue.setReplyText("проект buylist в todoist не существует,\n" +
					"прошу проследовать в режим первоначальной настройки /start");
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

	public boolean storageIsEmpty(Long userId) {
		return todoistStateMap.get(userId) == null || todoistStateMap.get(userId).isEmpty();
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
	                                                  List<SectionDto> sections,
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
	                                               List<SectionDto> sections,
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

	private void resultMessage(List<String> submittedTasks, ChatValue chatValue) {
		InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
				.keyboard(List.of(new InlineKeyboardRow(List.of(
						InlineKeyboardButton.builder()
								.text("показать полный список")
								.callbackData(Callbacks.VIEW.getCallbackData())
								.build()))))
				.build();
		StringBuilder sb = new StringBuilder();
		if (!submittedTasks.isEmpty()) {
			sb.append("были добавлены следующие задачи\n"); //todo добавить вывод тех которы не были добавлены(дубликаты)
			submittedTasks.forEach(t -> sb.append(" - ").append(t).append("\n"));
		} else {
			sb.append("задачи не были добавлены, т.к. они уже присутствуют в существующем списке");
		}
		chatValue.setReplyText(sb.toString());
		chatValue.setReplyKeyboard(keyboard);
	}

	private List<String> getNotExistSections(Map<String, Set<String>> inputTasks, List<SectionDto> sections) {
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
