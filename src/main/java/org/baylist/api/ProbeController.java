package org.baylist.api;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProbeController {

	private final ChatClient chatClient;

    @GetMapping("/test")
    public String test() {
        return "make love not war";
    }

	@GetMapping("/ai")
	public String ai() {
		String prompt = "если бы ты была jinx из сериала arcane, что бы ты мне сказала?. скажи это в ответ на следующее сообщение.";
		return chatClient.prompt(prompt)
				.user("привет")
				.call()
				.content();
	}


}
