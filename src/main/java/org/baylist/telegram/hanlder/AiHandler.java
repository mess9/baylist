package org.baylist.telegram.hanlder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.exception.AiException;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.baylist.ai.AiConfig.systemPrompt;
import static org.baylist.util.convert.ToJson.toJson;

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
		String inputText = chatValue.getInputText();
		ChatClient chatClient = chatClient(userId, inputText);

		String response;
		try {
			response = chatClient
					.prompt(prompt(chatValue))
					.user(inputText)
					.call()
					.content();
		} catch (AiException e) {
			log.error(e.getMessage(), e);
			response = "что-то на бекенде аи пошло не так :(";
		}
		assert response != null;

		chatValue.setReplyText(response);

		if (response.contains("**")) {
			chatValue.setReplyParseModeMarkdown();
		} else {
			if (response.contains("<ul>")) {
				response = response.replace("<ul>", "");
				response = response.replace("</ul>", "");
				chatValue.setReplyText(response);
			}
			chatValue.setReplyParseModeHtml();
		}
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
			chatMemoryMap.put(userId, new InMemoryChatMemory());
		}

		return ChatClient.builder(chatModel)
				.defaultSystem(systemPrompt())
				.defaultFunctions(functions())
				.defaultAdvisors(
						new SimpleLoggerAdvisor(),
						new MessageChatMemoryAdvisor(
								chatMemory,
								String.valueOf(userId),
								10)
				)
				.build();
	}

	@NotNull
	private Prompt prompt(ChatValue chatValue) {
		String promptModel = """
				currentUser -> {user}
				""";
		String userJson = toJson(chatValue.getUser());
		PromptTemplate promptTemplate = new PromptTemplate(promptModel);
		promptTemplate.add("user", userJson);
		return promptTemplate.create();
	}

	private String[] functions() {
		return List.of(
						"getTodoistData",
						"getAllTodoistData",
						"getDict",
						"getMyFriends",
						"getFriendsMe",
						"changeDict"
				)
				.toArray(new String[0]);
	}

}
