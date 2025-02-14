package org.baylist.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.ai.record.in.UserRequest;
import org.baylist.ai.record.in.UserRequestWithFriend;
import org.baylist.ai.record.in.UserRequestWithTask;
import org.baylist.ai.record.in.UserRequestWithTasks;
import org.baylist.ai.record.in.UserWithCategoryName;
import org.baylist.ai.record.in.UserWithCategoryRename;
import org.baylist.ai.record.in.UserWithChangeVariants;
import org.baylist.ai.record.in.UserWithDictRequest;
import org.baylist.ai.record.in.UserWithVariants;
import org.baylist.ai.record.out.ChangedVariants;
import org.baylist.ai.record.out.CreatedCategory;
import org.baylist.ai.record.out.CreatedVariants;
import org.baylist.ai.record.out.DeletedCategory;
import org.baylist.ai.record.out.DeletedFriend;
import org.baylist.ai.record.out.DeletedVariants;
import org.baylist.ai.record.out.Dictionary;
import org.baylist.ai.record.out.RenamedCategory;
import org.baylist.ai.record.out.SentTasks;
import org.baylist.ai.record.out.TodoistData;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.todoist.ProjectDto;
import org.baylist.dto.todoist.TodoistState;
import org.baylist.dto.todoist.api.TaskResponse;
import org.baylist.exception.AiException;
import org.baylist.service.DictionaryService;
import org.baylist.service.HistoryService;
import org.baylist.service.TodoistService;
import org.baylist.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiDataChanger {

	DictionaryService dictionaryService;
	UserService userService;
	TodoistService todoist;
	AiDataProvider aiDataProvider;
	HistoryService historyService;


	//region DICT

	@SuppressWarnings("unused")
	public Dictionary changeAllDict(UserWithDictRequest userRequest) {
		log.info("ai function called - changeAllDict");
		try {
			return new Dictionary(dictionaryService.changeAllDict(userRequest.user().getUserId(), userRequest.dict()));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public RenamedCategory renameCategory(UserWithCategoryRename userRequest) {
		log.info("ai function called - renameCategory");
		try {
			Category category = dictionaryService.getCategoryWithVariantsByName(userRequest.user().getUserId(),
					userRequest.originalCategoryName());
			dictionaryService.renameCategory(category, userRequest.newCategoryName());
			return new RenamedCategory(
					userRequest.originalCategoryName(),
					userRequest.newCategoryName());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public CreatedCategory createCategory(UserWithCategoryName userRequest) {
		log.info("ai function called - createCategory");
		try {
			dictionaryService.addCategory(userRequest.categoryName(), userRequest.user().getUserId());
			return new CreatedCategory(userRequest.categoryName());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public DeletedCategory deleteCategory(UserWithCategoryName userRequest) {
		log.info("ai function called - deleteCategory");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				dictionaryService.removeCategory(List.of(category));
				return new DeletedCategory(category.getName());
			} else {
				return new DeletedCategory("категория не была найдена и поэтому категория не была удалена");
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public ChangedVariants changeVariants(UserWithChangeVariants userRequest) {
		log.info("ai function called - changeVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				return new ChangedVariants(
						deleteVariants(
								new UserWithVariants(userRequest.user(),
										userRequest.categoryName(),
										userRequest.variantsForChange()))
								.deletedVariants(),
						createVariants(
								new UserWithVariants(userRequest.user(),
										userRequest.categoryName(),
										userRequest.variantsNewNames()))
								.createdVariants()
				);
			} else {
				return new ChangedVariants(
						List.of("категория не была найдена и поэтому варианты не были изменены"),
						List.of());
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public DeletedVariants deleteVariants(UserWithVariants userRequest) {
		log.info("ai function called - deleteVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				List<String> deletedVariants = dictionaryService
						.removeVariants(userRequest.variants(), category);
				return new DeletedVariants(deletedVariants);
			} else {
				return new DeletedVariants(List.of("категория не была найдена и поэтому варианты не были удалены"));
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public CreatedVariants createVariants(UserWithVariants userRequest) {
		log.info("ai function called - createVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				List<String> addedVariantsToCategory = dictionaryService
						.addVariantsToCategory(userRequest.variants(), category);
				return new CreatedVariants(addedVariantsToCategory);
			} else {
				return new CreatedVariants(List.of("категория не была найдена и поэтому варианты не были добавлены"));
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion DICT

	//region FRIENDS

	public DeletedFriend removeMyFriend(UserRequestWithFriend userRequest) {
		log.info("ai function called - removeMyFriend");
		try {
			String s = userService.removeMyFriend(userRequest.user().getUserId(), userRequest.friendName());
			return new DeletedFriend(s);
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public DeletedFriend removeFriendMe(UserRequestWithFriend userRequest) {
		log.info("ai function called - removeFriendMe");
		try {
			String s = userService.removeFromFriend(userRequest.user().getUserId(), userRequest.friendName());
			return new DeletedFriend(s);
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion FRIENDS

	//region TODOIST

	public TodoistData deleteTasksFromTodoist(UserRequestWithTasks userRequest) {
		log.info("ai function called - deleteTasksFromTodoist");
		try {
			TodoistData todoistData = aiDataProvider.getTodoistBuylistData(new UserRequest(userRequest.user()));
			List<String> tasks = userRequest.tasksNames();
			String token = "Bearer " + userRequest.user().getTodoistToken();
			List<ProjectDto> projects = todoistData.todoistState().getProjects();

			List<TaskResponse> tasksToRemove = projects.stream().flatMap(p -> p.getTasks().stream())
					.filter(t -> tasks.contains(t.getContent()))
					.toList();
			Map<String, Boolean> map = tasksToRemove.stream().map(t -> todoist.deleteTask(token, t))
					.collect(Collectors.toMap(k -> k.getValue().getId(), Map.Entry::getKey));
			historyService.sendTasks(userRequest.user(), userRequest.user(), Action.DELETE_TASK,
					map.entrySet().stream()
							.filter(e -> e.getValue().equals(true))
							.map(Map.Entry::getKey)
							.toList().toString());

			return new TodoistData(new TodoistState(projects.stream()
					.peek(p -> new ProjectDto(p.getProject(), p.getTasks().stream()
							.peek(t -> {
								if (map.containsKey(t.getId())) {
									Boolean isDeleted = map.get(t.getId());
									if (isDeleted) {
										t.setContent(t.getContent() + " задача успешно удалена");
									} else {
										t.setContent(t.getContent() + " удаление задачи провалено");
									}
								}
							}).toList(), p.getSections()))
					.toList()));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public SentTasks sendTaskToTodoist(UserRequestWithTasks userRequest) {
		log.info("ai function called - sendTaskToTodoist");
		try {
			User user = userRequest.user();
			log.info("token - {}", user.getTodoistToken());
			log.info("bearer token - {}", user.getBearerToken());
			if (todoist.storageIsEmpty(user.getUserId())) {
				todoist.syncBuyListData(user);
			}
			TodoistData todoistData = aiDataProvider.getTodoistBuylistData(new UserRequest(userRequest.user()));
			List<String> tasks = userRequest.tasksNames();
			Optional<ProjectDto> project = todoistData.todoistState().getBuyListProject();
			Map<String, Set<String>> inputTasks = dictionaryService.parseInputWithDict(tasks, user.getUserId());

			return new SentTasks(todoist.submitToTodoist(user, user, project, inputTasks));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public TaskResponse sendOneTaskToTodoist(UserRequestWithTask userRequest) {
		log.info("ai function called - sendOneTaskToTodoist");
		try {
			return todoist.sendOneTasksToTodoist(userRequest.user().getBearerToken(), userRequest.task());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}


	//endregion TODOIST

}
