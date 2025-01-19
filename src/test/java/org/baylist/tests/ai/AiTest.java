package org.baylist.tests.ai;

import org.baylist.ai.advisor.LoggingAdvisor;
import org.baylist.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

public class AiTest extends BaseTest {

	@Autowired
	ChatClient chatClient;
	LoggingAdvisor loggingAdvisor = new LoggingAdvisor();

	@Test
	void testAiLogging() {
		String hi = chatClient.prompt()
				.user("привет")
				.advisors(loggingAdvisor)
				.call()
				.content();

		s.assertThat(hi).containsIgnoringCase("привет");
	}

}
