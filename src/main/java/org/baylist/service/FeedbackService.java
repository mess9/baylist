package org.baylist.service;

import lombok.AllArgsConstructor;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.ChatState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;

@Component
@AllArgsConstructor
public class FeedbackService {

	UserService userService;

	public void acceptFeedback(ChatState chatState) {
		User fil = userService.getFil();
		chatState.setReplyText("""
				спасибо за обратную связь.
				сообщение будет передано куда надо
				""");
		ForwardMessage forwardMessage = ForwardMessage.builder()
				.chatId(fil.getUserId())
				.messageId(chatState.getUpdate().getMessage().getMessageId())
				.fromChatId(chatState.getUpdate().getMessage().getChatId())
				.build();
		chatState.setForwardMessage(forwardMessage);
		userService.feedbackOff(chatState);
	}

}
