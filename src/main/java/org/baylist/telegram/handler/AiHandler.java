package org.baylist.telegram.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.exception.AiException;
import org.baylist.telegram.handler.config.DialogHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.baylist.ai.AiConfig.functions;
import static org.baylist.ai.AiConfig.systemPrompt;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiHandler implements DialogHandler {

	ChatModel chatModel;
	Map<Long, ChatMemory> chatMemoryMap = new ConcurrentHashMap<>();

	@Override
	public void handle(ChatValue chatValue) {
		Long userId = chatValue.getUserId();
		if (checkUser(userId)) {
			String inputText = chatValue.getInputText();
			ChatClient chatClient = chatClient(userId, inputText);
			String response = chatCall(chatValue, chatClient, inputText);

			response = notAllowedSyntaxFilter(response);
			chatValue.setReplyText(response);
			chatValue.setReplyParseModeHtml();
		} else {
			chatValue.setReplyText("""
					сорян токены денег стоят
					не только лишь все могут юзать этот функционал
					пиши автору бота он может внести тебя в список избранных
					""");
		}
	}

	private boolean checkUser(Long userId) {
		return Arrays.stream(System.getenv("gpt").split(",")).map(Long::parseLong).toList().contains(userId);
	}

	@Nullable
	private String chatCall(ChatValue chatValue, ChatClient chatClient, String inputText) {
		String response;
		try {
			response = chatClient
					.prompt(prompt(chatValue))
					.user(inputText)
					.call()
					.content();
		} catch (AiException ai) {
			log.error(ai.getMessage(), ai);
			response = "что-то на бекенде при вызове функций аи пошло не так :(\nпопробуй переформулировать запрос";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = "что-то на бекенде аи пошло не так :(\nпопробуй переформулировать запрос";
		}
		return response;
	}

	private static String notAllowedSyntaxFilter(String text) {
		text = markdownRemove(text);
		text = removeSpecificHtmlTags(text);
		return text;

	}

	@NotNull
	private ChatClient chatClient(Long userId, String inputText) {
		ChatMemory chatMemory;

		if (chatMemoryMap.containsKey(userId)) {
			chatMemory = chatMemoryMap.get(userId);
		} else {
			chatMemory = new InMemoryChatMemory();
			chatMemoryMap.put(userId, chatMemory);
		}

		if (inputText.equals("аи")) {
			chatMemoryMap.put(userId, new InMemoryChatMemory()); //очистка памяти
		}

		return ChatClient.builder(chatModel)
				.defaultSystem(systemPrompt())
				.defaultFunctions(functions())
				.defaultAdvisors(
						new SimpleLoggerAdvisor(),
						new MessageChatMemoryAdvisor(
								chatMemory,
								String.valueOf(userId),
								100)
				)
				.build();
	}

	private static String markdownRemove(String text) {
		if (text.contains("**")) {
			Pattern MARKDOWN_PATTERN = Pattern.compile(
					"(" +
							"\\*\\*.*?\\*\\*" + // **жирный текст**
							"|\\*.*?\\*" + // *курсив*
							"|__.*?__" + // __жирный текст__
							"|_.*?_" + // _курсив_
							"|~~.*?~~" + // ~~зачеркнутый текст~~
							"|`.*?`" + // `код`
							"|```[\\s\\S]*?```" + // ```блок кода```
							"|\\n?#+ .*" + // # Заголовки
							"|\\[.*?]\\(.*?\\)" + // [текст ссылки](ссылка)
							"|!\\[.*?]\\(.*?\\)" + // ![изображение](ссылка)
							"|> .*" + // > цитаты
							"|- .*|\\* .*|\\d+\\. .*" + // списки
							")",
					Pattern.MULTILINE
			);
			return MARKDOWN_PATTERN.matcher(text).replaceAll("");
		} else {
			return text;
		}
	}

	private static String removeSpecificHtmlTags(String text) {
		String[] tagsToRemove = {"ul", "a", "li", "br", "tr"};

		// Проходим по каждому тегу и удаляем его
		for (String tag : tagsToRemove) {
			// Регулярное выражение для удаления только открывающих и закрывающих тегов
			String regex = "</?\\s*" + tag + "\\s*(/)?>";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);

			text = matcher.replaceAll("");
		}
		return text;
	}

	@NotNull
	private Prompt prompt(ChatValue chatValue) {
		String promptModel = """
				currentUser -> {user}
				currentTime -> {time}
				""";
		String userJson = chatValue.getUser().toString();
		PromptTemplate promptTemplate = new PromptTemplate(promptModel);
		promptTemplate.add("user", userJson);
		promptTemplate.add("time", OffsetDateTime.now());
		return promptTemplate.create();
	}
}

