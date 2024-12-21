package org.baylist.service;

import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;

@Component
public class ResponseService {

	//тут будет форматирование и локализация ответа

	public void cancelMessage(ChatValue chatValue) {
		chatValue.setReplyText("ок. в следующий раз будут деяния. а пока я отдохну");
		chatValue.setState(State.DEFAULT);
	}

}
