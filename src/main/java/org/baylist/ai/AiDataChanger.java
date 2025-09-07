//package org.baylist.ai;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.baylist.ai.record.in.UserRequest;
//import org.baylist.ai.record.in.UserRequestWithFriend;
//import org.baylist.ai.record.in.UserRequestWithTask;
//import org.baylist.ai.record.in.UserRequestWithTasks;
//import org.baylist.ai.record.in.UserWithCategoryName;
//import org.baylist.ai.record.in.UserWithCategoryRename;
//import org.baylist.ai.record.in.UserWithChangeVariants;
//import org.baylist.ai.record.in.UserWithDictRequest;
//import org.baylist.ai.record.in.UserWithVariants;
//import org.baylist.ai.record.out.ChangedVariants;
//import org.baylist.ai.record.out.CreatedCategory;
//import org.baylist.ai.record.out.CreatedVariants;
//import org.baylist.ai.record.out.DeletedCategory;
//import org.baylist.ai.record.out.DeletedFriend;
//import org.baylist.ai.record.out.DeletedVariants;
//import org.baylist.ai.record.out.Dictionary;
//import org.baylist.ai.record.out.RenamedCategory;
//import org.baylist.ai.record.out.SentTasks;
//import org.baylist.ai.record.out.TodoistData;
//import org.baylist.db.entity.Category;
//import org.baylist.db.entity.User;
//import org.baylist.dto.telegram.Action;
//import org.baylist.dto.todoist.ProjectDto;
//import org.baylist.dto.todoist.TodoistState;
//import org.baylist.dto.todoist.api.TaskResponse;
//import org.baylist.exception.AiException;
//import org.baylist.service.DictionaryService;
//import org.baylist.service.HistoryService;
//import org.baylist.service.TodoistService;
//import org.baylist.service.UserService;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//@Slf4j
//public class AiDataChanger {
//
//	DictionaryService dictionaryService;
//	UserService userService;
//	TodoistService todoist;
//	AiDataProvider aiDataProvider;
//	HistoryService historyService;
//
//
//	//region DICT
//
//	@SuppressWarnings("unused")
//	public Dictionary changeAllDict(UserWithDictRequest userRequest) {
//		log.info("ai function called - changeAllDict");
//		try {
//			return new Dictionary(dictionaryService.changeAllDict(userRequest.user().userId(), userRequest.dict()));
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			изменение имени одной категории
//			изменяется старое имя категории на новое имя категории
//			""")
//	public RenamedCategory renameCategory(UserWithCategoryRename userRequest) {
//		log.info("ai function called - renameCategory");
//		try {
//			Category category = dictionaryService.getCategoryWithVariantsByName(userRequest.user().userId(),
//					userRequest.originalCategoryName());
//			dictionaryService.renameCategory(category, userRequest.newCategoryName());
//			return new RenamedCategory(
//					userRequest.originalCategoryName(),
//					userRequest.newCategoryName());
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			добавление одной новой пустой категории в словарик
//			""")
//	public CreatedCategory createCategory(UserWithCategoryName userRequest) {
//		log.info("ai function called - createCategory");
//		try {
//			dictionaryService.addCategory(userRequest.categoryName(), userRequest.user().userId());
//			return new CreatedCategory(userRequest.categoryName());
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			удаление одной категории вместе со всеми вариантами
//			удалять только при однозначной формулировке этого действия
//			""")
//	public DeletedCategory deleteCategory(UserWithCategoryName userRequest) {
//		log.info("ai function called - deleteCategory");
//		try {
//			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().userId()).stream()
//					.filter(c -> c.name().equals(userRequest.categoryName()))
//					.findAny()
//					.orElse(null);
//			if (category != null) {
//				dictionaryService.removeCategory(List.of(category));
//				return new DeletedCategory(category.name());
//			} else {
//				return new DeletedCategory("категория не была найдена и поэтому категория не была удалена");
//			}
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			изменение существующих вариантов внутри указанной категории
//			переименование нескольких вариантов сразу
//			List<String> variantsForChange - варианты которые нужно переименовать/изменить
//			List<String> variantsNewNames - новые имена для вариантов которые нужно переименовать/изменить
//			проверить существует ли категория - можно функцией getDictOnlyAllCategories
//			проверить какие варианты сейчас есть в категории - можно функцией getDictOneCategoryWithVariants
//			переименовывать/изменять только при однозначной формулировке этого действия
//			""")
//	public ChangedVariants changeVariants(UserWithChangeVariants userRequest) {
//		log.info("ai function called - changeVariants");
//		try {
//			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().userId()).stream()
//					.filter(c -> c.name().equals(userRequest.categoryName()))
//					.findAny()
//					.orElse(null);
//			if (category != null) {
//				return new ChangedVariants(
//						deleteVariants(
//								new UserWithVariants(userRequest.user(),
//										userRequest.categoryName(),
//										userRequest.variantsForChange()))
//								.deletedVariants(),
//						createVariants(
//								new UserWithVariants(userRequest.user(),
//										userRequest.categoryName(),
//										userRequest.variantsNewNames()))
//								.createdVariants()
//				);
//			} else {
//				return new ChangedVariants(
//						List.of("категория не была найдена и поэтому варианты не были изменены"),
//						List.of());
//			}
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			удаление существующих вариантов из указанной категории
//			проверить существует ли категория - можно функцией getDictOnlyAllCategories
//			перед удалением получить информацию о том какие сейчас варианты есть внутри указанной категории - можно функцией getDictOneCategoryWithVariants
//			удалять только при однозначной формулировке этого действия
//			""")
//	public DeletedVariants deleteVariants(UserWithVariants userRequest) {
//		log.info("ai function called - deleteVariants");
//		try {
//			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().userId()).stream()
//					.filter(c -> c.name().equals(userRequest.categoryName()))
//					.findAny()
//					.orElse(null);
//			if (category != null) {
//				List<String> deletedVariants = dictionaryService
//						.removeVariants(userRequest.variants(), category);
//				return new DeletedVariants(deletedVariants);
//			} else {
//				return new DeletedVariants(List.of("категория не была найдена и поэтому варианты не были удалены"));
//			}
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			изменение словарика пользователя
//			добавление новых вариантов в указанную категорию
//			проверить существует ли категория - можно функцией getDictOnlyAllCategories
//			""")
//	public CreatedVariants createVariants(UserWithVariants userRequest) {
//		log.info("ai function called - createVariants");
//		try {
//			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().userId()).stream()
//					.filter(c -> c.name().equals(userRequest.categoryName()))
//					.findAny()
//					.orElse(null);
//			if (category != null) {
//				List<String> addedVariantsToCategory = dictionaryService
//						.addVariantsToCategory(userRequest.variants(), category);
//				return new CreatedVariants(addedVariantsToCategory);
//			} else {
//				return new CreatedVariants(List.of("категория не была найдена и поэтому варианты не были добавлены"));
//			}
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	//endregion DICT
//
//	//region FRIENDS
//
//	@Tool(description = """
//			удалить одного друга который может отправлять/добавлять мне задачи
//			удалять только при однозначной формулировке этого действия
//			""")
//	public DeletedFriend removeMyFriend(UserRequestWithFriend userRequest) {
//		log.info("ai function called - removeMyFriend");
//		try {
//			String s = userService.removeMyFriend(userRequest.user().userId(), userRequest.friendName());
//			return new DeletedFriend(s);
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			удалить одного друга которому я могу отправлять/добавлять задачи
//			удалять только при однозначной формулировке этого действия
//			""")
//	public DeletedFriend removeFriendMe(UserRequestWithFriend userRequest) {
//		log.info("ai function called - removeFriendMe");
//		try {
//			String s = userService.removeFromFriend(userRequest.user().userId(), userRequest.friendName());
//			return new DeletedFriend(s);
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	//endregion FRIENDS
//
//	//region TODOIST
//
//	@Tool(description = """
//			удалить задачи из todoist
//			для авторизации используется - todoistToken
//			""")
//	public TodoistData deleteTasksFromTodoist(UserRequestWithTasks userRequest) {
//		log.info("ai function called - deleteTasksFromTodoist");
//		try {
//			TodoistData todoistData = aiDataProvider.getTodoistBuylistData(new UserRequest(userRequest.user()));
//			List<String> tasks = userRequest.tasksNames();
//			String token = "Bearer " + userRequest.user().todoistToken();
//			List<ProjectDto> projects = todoistData.todoistState().getProjects();
//
//			List<TaskResponse> tasksToRemove = projects.stream().flatMap(p -> p.getTasks().stream())
//					.filter(t -> tasks.contains(t.getContent()))
//					.toList();
//			Map<String, Boolean> map = tasksToRemove.stream().map(t -> todoist.deleteTask(token, t))
//					.collect(Collectors.toMap(k -> k.getValue().getId(), Map.Entry::getKey));
//			historyService.sendTasks(userRequest.user(), userRequest.user(), Action.DELETE_TASK,
//					map.entrySet().stream()
//							.filter(e -> e.getValue().equals(true))
//							.map(Map.Entry::getKey)
//							.toList().toString());
//
//			return new TodoistData(new TodoistState(projects.stream()
//					.peek(p -> new ProjectDto(p.getProject(), p.getTasks().stream()
//							.peek(t -> {
//								if (map.containsKey(t.getId())) {
//									Boolean isDeleted = map.get(t.getId());
//									if (isDeleted) {
//										t.setContent(t.getContent() + " задача успешно удалена");
//									} else {
//										t.setContent(t.getContent() + " удаление задачи провалено");
//									}
//								}
//							}).toList(), p.getSections()))
//					.toList()));
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			добавить задачи в todoist
//			для авторизации используется - todoistToken
//
//			задачи будут автоматически распределены в проекте todoist согласованно словарю пользователя
//			посмотреть словарь пользователя - функция getAllDict
//			ЕСЛИ задача должна быть добавлена в новую категорию то: {
//			в начале/до/before отправки в todoist
//			нужно создать нужную категорию в словаре пользователя - функция createCategory
//			добавить в созданную категорию вариант этой задачи - функция createVariants
//			и только ПОСЛЕ - отправить задачу в todoist }
//			ЕСЛИ задача должна быть добавлена в существующую категорию но в этой категории ещё нет варианта этой задачи {
//			добавить в существующую категорию вариант этой задачи - функция createVariants
//			и только ПОСЛЕ - отправить задачу в todoist }
//			ЕСЛИ задача присутствует в словаре пользователя {
//			отправить её в todoist }
//			ЕСЛИ не указано в какую категорию нужно поместить задачу
//			или прямо сказано что у задачи не должно быть категорий {
//			отправить задачу в todoist }
//			""")
//	public SentTasks sendTaskToTodoist(UserRequestWithTasks userRequest) {
//		log.info("ai function called - sendTaskToTodoist");
//		try {
//			User user = userRequest.user();
//			log.info("token - {}", user.todoistToken());
//			log.info("bearer token - {}", user.bearerToken());
//			if (todoist.storageIsEmpty(user.userId())) {
//				todoist.syncBuyListData(user);
//			}
//			TodoistData todoistData = aiDataProvider.getTodoistBuylistData(new UserRequest(userRequest.user()));
//			List<String> tasks = userRequest.tasksNames();
//			Optional<ProjectDto> project = todoistData.todoistState().getBuyListProject();
//			Map<String, Set<String>> inputTasks = dictionaryService.parseInputWithDict(tasks, user.userId());
//
//			return new SentTasks(todoist.submitToTodoist(user, user, project, inputTasks));
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//	@Tool(description = """
//			только для задач у которых есть дата выполнения
//			не нужны категории. не использовать словарик.
//			для авторизации используется - todoistToken
//
//			заполнить все нужные поля в Task
//			content - название задачи
//			priority - Task priority from 1 (normal) to 4 (urgent)
//			due_datetime - Specific date and time in RFC3339 format in UTC
//			""")
//	public TaskResponse sendOneTaskToTodoist(UserRequestWithTask userRequest) {
//		log.info("ai function called - sendOneTaskToTodoist");
//		try {
//			return todoist.sendOneTasksToTodoist(userRequest.user().bearerToken(), userRequest.task());
//		} catch (Exception e) {
//			throw new AiException(e.getMessage());
//		}
//	}
//
//
//	//endregion TODOIST
//
//}
