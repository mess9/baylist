package org.baylist.tests.ai;

import org.baylist.ai.AiDataProvider;
import org.baylist.ai.record.in.UserRequest;
import org.baylist.ai.record.out.TodoistData;
import org.baylist.db.entity.User;
import org.baylist.dto.todoist.TodoistState;
import org.baylist.tests.BaseTest;
import org.baylist.util.extension.FilToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

public class AiTest extends BaseTest {

	@Autowired
	ChatModel chatModel;
	ChatClient chatClient;

	@Autowired
	AiDataProvider aiDataProvider;

	@FilToken
	String token;

	@BeforeEach
	void setup() {
		chatClient = ChatClient.builder(chatModel).build();
	}

//	@Test
//	void givenMessageChatMemoryAdvisor_whenAskingChatToIncrementTheResponseWithNewName_thenNamesFromTheChatHistoryExistInResponse() {
//		ChatMemory chatMemory = new InMemoryChatMemory();
//		ChatMemory chatMemory2 = new InMemoryChatMemory();
//		MessageChatMemoryAdvisor chatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
//		MessageChatMemoryAdvisor chatMemoryAdvisor2 = new MessageChatMemoryAdvisor(chatMemory2);
//
//		String responseContent = chatClient.prompt()
//				.user("Add this name to a list and return all the values: Bob")
//				.advisors(chatMemoryAdvisor)
//				.call()
//				.content();
//
//		s.assertThat(responseContent)
//				.contains("Bob");
//
//		responseContent = chatClient.prompt()
//				.user("Add this name to a list and return all the values: John")
//				.advisors(chatMemoryAdvisor)
//				.call()
//				.content();
//
//		s.assertThat(responseContent)
//				.contains("Bob")
//				.contains("John");
//
//		responseContent = chatClient.prompt()
//				.user("Add this name to a list and return all the values: Anna")
//				.advisors(chatMemoryAdvisor)
//				.call()
//				.content();
//
//		s.assertThat(responseContent)
//				.contains("Bob")
//				.contains("John")
//				.contains("Anna");
//
//		responseContent = chatClient.prompt()
//				.user("Add this name to a list and return all the values: Anna")
//				.advisors(chatMemoryAdvisor2)
//				.call()
//				.content();
//
//		System.out.println(responseContent);
//	}

	@Test
	void dataProvider() {
		String[] split = token.split(" ");
		User user = new User();
		user.setTodoistToken(split[1]);
		TodoistData allTodoistData = aiDataProvider.getAllTodoistData(new UserRequest(user));
		TodoistState todoistState = allTodoistData.todoistState();
		todoistState.getProjects().forEach(System.out::println);
	}
}

