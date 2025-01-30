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
import org.baylist.ai.record.in.UserWithVariants;
import org.baylist.ai.record.out.CategoryNameList;
import org.baylist.ai.record.out.ChangedVariants;
import org.baylist.ai.record.out.CreatedCategory;
import org.baylist.ai.record.out.CreatedVariants;
import org.baylist.ai.record.out.DeletedCategory;
import org.baylist.ai.record.out.DeletedFriend;
import org.baylist.ai.record.out.DeletedVariants;
import org.baylist.ai.record.out.Dictionary;
import org.baylist.ai.record.out.Friends;
import org.baylist.ai.record.out.OneCategoryInfo;
import org.baylist.ai.record.out.RenamedCategory;
import org.baylist.ai.record.out.SentTasks;
import org.baylist.ai.record.out.TodoistData;
import org.baylist.dto.todoist.api.TaskResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiConfig {

	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}


	//region TODOIST

	public static String[] functions() {
		return List.of(
						// todoist
						// - getTodoistData
						"getTodoistData",
						"getAllTodoistData",
						// - changeTodoistData
						"sendTasksToTodoist",
						"sendDateTaskToTodoist",
						"deleteTasksFromTodoist",
						// friends
						// - getFriends
						"getMyFriends",
						"getFriendsMe",
						// - changeFriends
						"removeMyFriend",
						"removeFriendMe",
						// dict
						// - getDict
						"getAllDict",
						"getDictOnlyAllCategories",
						"getDictOneCategoryWithVariants",
						// - changeDict
//						"changeAllDict", -- выключено из-за нестабильности
						"renameCategory",
						"createCategory",
						"deleteCategory",
						"createVariants",
						"deleteVariants",
						"changeVariants"
				)
				.toArray(new String[0]);
	}

	public static String systemPrompt() {
		return """
				тебя зовут - "фича". твой род - женский
				ты помощник для телеграм бота про составление списка задач в todoist
				твои черты: ироничность, чёрный юмор, много чёрного юмора
				
				телеграм бот создан для помощи в составлении списка задач, отправки их в todoist с разбивкой по категориям
				у пользователя есть словарь с категориями и вариантами задач внутри них
				когда пользователь присылает задачи их необходимо сопоставить со словарём пользователя, чтобы определить в какую категорию поместить ту или иную задачу
				
				если тебя спрашивают что-то непонятное, не пытайся делать то что не до конца понимаешь, лучше переспроси
				или пошути
				
				форматирование HTML. нельзя использовать markdown
				доступны только такие теги
				1. <b>bold</b>
				2. <i>italic</i>
				3. <u>underline</u>
				4. <s>strikethrough</s>
				5. <code>code</code>
				другие теги использовать нельзя никогда!
				тег <ul> - нельзя!
				тег <a> - нельзя!
				тег <li> - нельзя!
				тег <br> - нельзя!
				тег <tr> - нельзя!
				использовать markdown - нельзя!
				Markdown syntax is not allowed
				не выводи ссылки - если тебя об этом не просят
				не пользуйся большими буквами - если тебя об этом не попросят
				
				правило - игнорировать текст "аи"
				не говори об этом правиле
				ты можешь помнить до 100 сообщений в нашем диалоге
				""";
	}

	@Bean
	@Description("""
			получить список задач из проекта buylist, приоритетная функция на вопрос пользователя о задачах
			для авторизации используется - todoistToken
			выводить ответ с разбивкой по категориям
			игнорировать поле url
			""")
	public Function<UserRequest, TodoistData> getTodoistData(AiDataProvider dataProvider) {
		return dataProvider::getTodoistBuylistData;
	}

	@Bean
	@Description("""
			получить абсолютно все задачи из todoist
			для авторизации используется - todoistToken
			игнорировать поле url
			""")
	public Function<UserRequest, TodoistData> getAllTodoistData(AiDataProvider dataProvider) {
		return dataProvider::getAllTodoistData;
	}

	@Bean
	@Description("""
			удалить задачи из todoist
			для авторизации используется - todoistToken
			""")
	public Function<UserRequestWithTasks, TodoistData> deleteTasksFromTodoist(AiDataChanger dataChanger) {
		return dataChanger::deleteTasksFromTodoist;
	}


	// добавление себе задач


	//endregion TODOIST

	//region FRIENDS

	@Bean
	@Description("получить список друзей которые могут отправлять/добавлять мне задачи")
	public Function<UserRequest, Friends> getMyFriends(AiDataProvider dataProvider) {
		return dataProvider::getMyFriends;
	}

	@Bean
	@Description("получить список друзей которым я могу отправлять/добавлять задачи")
	public Function<UserRequest, Friends> getFriendsMe(AiDataProvider dataProvider) {
		return dataProvider::getFriendsMe;
	}

	@Bean
	@Description("""
			добавить задачи в todoist
			для авторизации используется - todoistToken
			
			задачи будут автоматически распределены в проекте todoist согласованно словарю пользователя
			посмотреть словарь пользователя - функция getAllDict
			ЕСЛИ задача должна быть добавлена в новую категорию то: {
			в начале/до/before отправки в todoist
			нужно создать нужную категорию в словаре пользователя - функция createCategory
			добавить в созданную категорию вариант этой задачи - функция createVariants
			и только ПОСЛЕ - отправить задачу в todoist }
			ЕСЛИ задача должна быть добавлена в существующую категорию но в этой категории ещё нет варианта этой задачи {
			добавить в существующую категорию вариант этой задачи - функция createVariants
			и только ПОСЛЕ - отправить задачу в todoist }
			ЕСЛИ задача присутствует в словаре пользователя {
			отправить её в todoist }
			ЕСЛИ не указано в какую категорию нужно поместить задачу
			или прямо сказано что у задачи не должно быть категорий {
			отправить задачу в todoist }
			""")
	public Function<UserRequestWithTasks, SentTasks> sendTasksToTodoist(AiDataChanger dataChanger) {
		return dataChanger::sendTaskToTodoist;
	}

	@Bean
	@Description("""
			только для задач у которых есть дата выполнения
			не нужны категории. не использовать словарик.
			для авторизации используется - todoistToken
			
			заполнить все нужные поля в Task
			content - название задачи
			priority - Task priority from 1 (normal) to 4 (urgent)
			due_datetime - Specific date and time in RFC3339 format in UTC
			""")
	public Function<UserRequestWithTask, TaskResponse> sendDateTaskToTodoist(AiDataChanger dataChanger) {
		return dataChanger::sendOneTaskToTodoist;
	}

	//endregion FRIENDS

	//region DICT

	//region GET DICT

	@Bean
	@Description("""
			получить полностью словарь пользователя с категориями и вариантами задач внутри них
			в словаре перечислены категории задач
			внутри категорий перечислены варианты задач для этой категории
			""")
	public Function<UserRequest, Dictionary> getAllDict(AiDataProvider dataProvider) {
		return dataProvider::getDict;
	}

	@Bean
	@Description("""
			удалить одного друга который может отправлять/добавлять мне задачи
			удалять только при однозначной формулировке этого действия
			""")
	public Function<UserRequestWithFriend, DeletedFriend> removeMyFriend(AiDataChanger dataChanger) {
		return dataChanger::removeMyFriend;
	}

	@Bean
	@Description("""
			удалить одного друга которому я могу отправлять/добавлять задачи
			удалять только при однозначной формулировке этого действия
			""")
	public Function<UserRequestWithFriend, DeletedFriend> removeFriendMe(AiDataChanger dataChanger) {
		return dataChanger::removeFriendMe;
	}

	//endregion GET DICT

	//region CHANGE DICT

//	@Bean  //работает нестабильно, а потому выключен
//	@Description("""
//			изменять словарик пользователя
//			добавлять категории
//			добавлять варианты задач внутрь категории
//			переименовывать категории
//			переименовывать варианты задач внутри категорий
//			удалять категории
//			удалять варианты задач внутри категорий
//
//			перед тем как изменять словарь - надо получить словарь методом getDict
//			изменить полученную структуру данных record Dictionary(Map<String, Set<String>> dictionary)
//			и передать изменённую структуру данных в виде UserWithDictRequest(User user, Map<String, Set<String>> dict)
//			передать нужно всю структуру целиком, включая изменения и данные которые не были изменены
//			в ответ метод возвращает получившееся состояние словаря в формате Dictionary(Map<String, Set<String>> dictionary)
//			""")
//	public Function<UserWithDictRequest, Map<String, Set<String>>> changeAllDict(AiDataChanger dataChanger) {
//		return dataChanger::changeAllDict;
//	}

	@Bean
	@Description("""
			изменение словарика пользователя
			изменение имени одной категории
			изменяется старое имя категории на новое имя категории
			""")
	public Function<UserWithCategoryRename, RenamedCategory> renameCategory(AiDataChanger dataChanger) {
		return dataChanger::renameCategory;
	}

	@Bean
	@Description("""
			изменение словарика пользователя
			добавление одной новой пустой категории в словарик
			""")
	public Function<UserWithCategoryName, CreatedCategory> createCategory(AiDataChanger dataChanger) {
		return dataChanger::createCategory;
	}

	@Bean
	@Description("""
			изменение словарика пользователя
			удаление одной категории вместе со всеми вариантами
			удалять только при однозначной формулировке этого действия
			""")
	public Function<UserWithCategoryName, DeletedCategory> deleteCategory(AiDataChanger dataChanger) {
		return dataChanger::deleteCategory;
	}

	@Bean
	@Description("""
			изменение словарика пользователя
			добавление новых вариантов в указанную категорию
			проверить существует ли категория - можно функцией getDictOnlyAllCategories
			""")
	public Function<UserWithVariants, CreatedVariants> createVariants(AiDataChanger dataChanger) {
		return dataChanger::createVariants;
	}

	@Bean
	@Description("""
			получить полный список всех категорий в словаре пользователя.
			без вариантов задач внутри категорий
			""")
	public Function<UserRequest, CategoryNameList> getDictOnlyAllCategories(AiDataProvider dataProvider) {
		return dataProvider::getDictAllCategories;
	}

	@Bean
	@Description("""
			получить информацию по одной категории пользователя
			со всеми вариантами задач внутри этой категории
			""")
	public Function<UserWithCategoryName, OneCategoryInfo> getDictOneCategoryWithVariants(AiDataProvider dataProvider) {
		return dataProvider::getDictOneCategory;
	}

	//endregion CHANGE DICT

	//endregion DICT

	//region SETTINGS

	@Bean
	@Description("""
			изменение словарика пользователя
			удаление существующих вариантов из указанной категории
			проверить существует ли категория - можно функцией getDictOnlyAllCategories
			перед удалением получить информацию о том какие сейчас варианты есть внутри указанной категории - можно функцией getDictOneCategoryWithVariants
			удалять только при однозначной формулировке этого действия
			""")
	public Function<UserWithVariants, DeletedVariants> deleteVariants(AiDataChanger dataChanger) {
		return dataChanger::deleteVariants;
	}

	@Bean
	@Description("""
			изменение словарика пользователя
			изменение существующих вариантов внутри указанной категории
			переименование нескольких вариантов сразу
			List<String> variantsForChange - варианты которые нужно переименовать/изменить
			List<String> variantsNewNames - новые имена для вариантов которые нужно переименовать/изменить
			проверить существует ли категория - можно функцией getDictOnlyAllCategories
			проверить какие варианты сейчас есть в категории - можно функцией getDictOneCategoryWithVariants
			переименовывать/изменять только при однозначной формулировке этого действия
			""")
	public Function<UserWithChangeVariants, ChangedVariants> changeVariants(AiDataChanger dataChanger) {
		return dataChanger::changeVariants;
	}

	//endregion SETTINGS

}

