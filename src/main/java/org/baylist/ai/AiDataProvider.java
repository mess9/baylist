package org.baylist.ai;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.ai.record.in.UserRequest;
import org.baylist.ai.record.in.UserWithCategoryName;
import org.baylist.ai.record.out.CategoryNameList;
import org.baylist.ai.record.out.Dictionary;
import org.baylist.ai.record.out.Friends;
import org.baylist.ai.record.out.OneCategoryInfo;
import org.baylist.ai.record.out.TodoistData;
import org.baylist.ai.record.wrapdto.UserDto;
import org.baylist.api.TodoistFeignClient;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.User;
import org.baylist.db.entity.Variant;
import org.baylist.dto.todoist.TodoistState;
import org.baylist.dto.todoist.api.Project;
import org.baylist.dto.todoist.api.Section;
import org.baylist.dto.todoist.api.TaskResponse;
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

	//region TODOIST

	public TodoistData getTodoistBuylistData(UserRequest userRequest) {
		log.info("ai function called - getTodoistData project buylist");
		try {
			String token = "Bearer " + userRequest.user().getTodoistToken();

			List<Project> projects = todoistApi.getProjects(token);
			Optional<Project> buylistProject = projects
					.stream().filter(p -> p.getName().equalsIgnoreCase(BUYLIST_PROJECT))
					.findAny();
			if (buylistProject.isPresent()) {
				String projectId = buylistProject.get().getId();
				List<Section> todoistBuylistSections = todoistApi.getSectionsByProject(token, projectId);
				List<TaskResponse> todoistBuylistTasks = todoistApi.getTasksByProject(token, projectId);
				return new TodoistData(new TodoistState(projects, todoistBuylistSections, todoistBuylistTasks));
			} else {
				log.error("buylist project not found");
				return null;
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public TodoistData getAllTodoistData(UserRequest userRequest) {
		log.info("ai function called - getAllTodoistData");
		try {
			String token = "Bearer " + userRequest.user().getTodoistToken();

			List<Project> projects = todoistApi.getProjects(token);
			List<Section> sections = new ArrayList<>();
			List<TaskResponse> tasks = new ArrayList<>();

			projects.forEach(p -> {
				sections.addAll(todoistApi.getSectionsByProject(token, p.getId()));
				tasks.addAll(todoistApi.getTasksByProject(token, p.getId()));
			});

			return new TodoistData(new TodoistState(projects, sections, tasks));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion TODOIST

	//region FRIENDS

	@Transactional
	public Friends getMyFriends(UserRequest userRequest) {
		log.info("ai function called - getMyFriends");
		try {
			User user = userService.getUserFromWithFriendsDb(userRequest.user().getUserId());
			List<UserDto> list = user.getFriends().stream().map(f -> new UserDto().convertToDDto(f)).toList();
			return new Friends(list);
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public Friends getFriendsMe(UserRequest userRequest) {
		log.info("ai function called - getFriendsMe");
		try {
			List<User> friendMe = userService.getFriendMe(userRequest.user().getUserId());
			List<UserDto> list = friendMe.stream().map(f -> new UserDto().convertToDDto(f)).toList();
			return new Friends(list);
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion FRIENDS

	//region DICT

	public Dictionary getDict(UserRequest userRequest) {
		log.info("ai function called - getDict");
		try {
			return new Dictionary(dictionaryService.getDict(userRequest.user().getUserId()));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public CategoryNameList getDictAllCategories(UserRequest userRequest) {
		log.info("ai function called - getDictAllCategories");
		try {
			return new CategoryNameList(dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.map(Category::getName).toList());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public OneCategoryInfo getDictOneCategory(UserWithCategoryName userRequest) {
		log.info("ai function called - getDictOneCategory");
		try {
			Category categoryWithVariants = dictionaryService
					.getCategoryWithVariantsByName(userRequest.user().getUserId(), userRequest.categoryName());
			return new OneCategoryInfo(
					categoryWithVariants.getName(),
					categoryWithVariants.getVariants().stream().map(Variant::getName).toList());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion DICT

}
