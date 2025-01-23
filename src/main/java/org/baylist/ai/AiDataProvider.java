package org.baylist.ai;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.ai.record.Dictionary;
import org.baylist.ai.record.Friends;
import org.baylist.ai.record.TodoistData;
import org.baylist.ai.record.UserDto;
import org.baylist.ai.record.UserRequest;
import org.baylist.api.TodoistFeignClient;
import org.baylist.db.entity.User;
import org.baylist.dto.todoist.TodoistState;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.Task;
import org.baylist.exception.AiException;
import org.baylist.service.DictionaryService;
import org.baylist.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.baylist.dto.Constants.BUYLIST_PROJECT;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiDataProvider {

	DictionaryService dictionaryService;
	UserService userService;

	TodoistFeignClient todoistApi;


	public TodoistData getTodoistBuylistData(UserRequest userRequest) throws AiException {
		String token = userRequest.user().getBearerToken();
		log.info("ai request to get data from todoist");

		List<Project> projects = todoistApi.getProjects(token);
		Optional<Project> buylistProject = projects
				.stream().filter(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))
				.findAny();
		if (buylistProject.isPresent()) {
			String projectId = buylistProject.get().getId();
			List<Section> todoistBuylistSections = todoistApi.getSectionsByProject(token, projectId);
			List<Task> todoistBuylistTasks = todoistApi.getTasksByProject(token, projectId);
			return new TodoistData(new TodoistState(projects, todoistBuylistSections, todoistBuylistTasks));
		} else {
			log.error("buylist project not found");
			return null;
		}
	}

	public TodoistData getAllTodoistData(UserRequest userRequest) throws AiException {
		String token = userRequest.user().getBearerToken();
		log.info("ai request to get all data from todoist");

		List<Project> projects = todoistApi.getProjects(token);
		List<Section> sections = new ArrayList<>();
		List<Task> tasks = new ArrayList<>();

		projects.forEach(p -> {
			sections.addAll(todoistApi.getSectionsByProject(token, p.getId()));
			tasks.addAll(todoistApi.getTasksByProject(token, p.getId()));
		});

		return new TodoistData(new TodoistState(projects, sections, tasks));
	}

	public Dictionary getDict(UserRequest userRequest) throws AiException {
		return new Dictionary(dictionaryService.getDict(userRequest.user().getUserId()));
	}

	@Transactional
	public Friends getMyFriends(UserRequest userRequest) throws AiException {
		User user = userService.getUserFromWithFriendsDb(userRequest.user().getUserId());
		List<UserDto> list = user.getFriends().stream().map(f -> new UserDto().convertToDDto(f)).toList();
		return new Friends(list);
	}

	public Friends getFriendsMe(UserRequest userRequest) throws AiException {
		List<User> friendMe = userService.getFriendMe(userRequest.user().getUserId());
		List<UserDto> list = friendMe.stream().map(f -> new UserDto().convertToDDto(f)).toList();
		return new Friends(list);
	}

}
