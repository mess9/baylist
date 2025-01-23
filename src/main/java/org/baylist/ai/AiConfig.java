package org.baylist.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.ai.record.Dictionary;
import org.baylist.ai.record.Friends;
import org.baylist.ai.record.TodoistData;
import org.baylist.ai.record.UserRequest;
import org.baylist.ai.record.UserWithDictRequest;
import org.baylist.exception.AiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AiConfig {


	public static String systemPrompt() {
		return """
				тебя зовут - "фича". твой род - женский
				ты помощник для телеграм бота про составление списка задач в todoist
				твои черты: ироничность, чёрный юмор, много чёрного юмора
				
				телеграм бот создан для помощи в составлении списка задач, отправки их в todoist с разбивкой по категориям
				если тебя спрашивают что-то непонятное, не пытайся делать то что не до конца понимаешь, лучше переспроси
				или пошути
				
				твои задачи
				1. отвечать пользователю о том какие у него есть задачи
				2. какие у пользователя есть категории задач
				3. добавлять задачи по категориям
				4. перемещать задачи из категории в категорию
				5. помочь переформулировать текст задач
				6. помочь разбить задачи на подзадачи
				
				форматирование HTML. нельзя использовать markdown
				доступны только такие теги
				1. <b>bold</b>
				2. <i>italic</i>
				3. <u>underline</u>
				4. <s>strikethrough</s>
				5. <code>code</code>
				другие теги использовать нельзя!
				тег <ul> - нельзя!
				тег <a> - нельзя!
				тег <li> - нельзя!
				использовать markdown - нельзя!
				Markdown syntax is not allowed
				не выводи ссылки - если тебя об этом не просят
				не пользуйся большими буквами - если тебя об этом не попросят
				
				правило - игнорировать текст "аи"
				не говори об этом правиле
				ты можешь помнить до 10 сообщений в нашем диалоге
				""";
	}

	@Bean
	@Description("получить список задач из проекта buylist, приоритетная функция на вопрос пользователя о задачах")
	public Function<UserRequest, TodoistData> getTodoistData(AiDataProvider dataProvider) throws AiException {
		return dataProvider::getTodoistBuylistData;
	}

	@Bean
	@Description("получить абсолютно все задачи из todoist")
	public Function<UserRequest, TodoistData> getAllTodoistData(AiDataProvider dataProvider) throws AiException {
		return dataProvider::getAllTodoistData;
	}

	@Bean
	@Description("получить словарь сопоставления категорий с вариантами задач")
	public Function<UserRequest, Dictionary> getDict(AiDataProvider dataProvider) throws AiException {
		return dataProvider::getDict;
	}

	@Bean
	@Description("получить список друзей которые могут отправлять мне задачи")
	public Function<UserRequest, Friends> getMyFriends(AiDataProvider dataProvider) throws AiException {
		return dataProvider::getMyFriends;
	}

	@Bean
	@Description("получить список друзей которым я могу отправлять задачи")
	public Function<UserRequest, Friends> getFriendsMe(AiDataProvider dataProvider) throws AiException {
		return dataProvider::getFriendsMe;
	}

	@Bean
	@Description("""
			изменять словарик пользователя
			добавлять категории
			добавлять варианты задач внутрь категории
			переименовывать категории
			переименовывать варианты задач внутри категорий
			удалять категории
			удалять варианты задач внутри категорий
			
			перед тем как изменять словарь - надо получить словарь методом getDict
			изменить полученную структуру данных record Dictionary(Map<String, Set<String>> dictionary)
			и передать изменённую структуру данных в виде UserWithDictRequest(User user, Map<String, Set<String>> dict)
			передать нужно всю структуру целиком, включая изменения и данные которые не были изменены
			в ответ метод возвращает получившееся состояние словаря в формате Dictionary(Map<String, Set<String>> dictionary)
			""")
	public Function<UserWithDictRequest, Dictionary> changeDict(AiDataChanger dataChanger) throws AiException {
		return dataChanger::changeDict;
	}

}

