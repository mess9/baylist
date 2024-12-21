package org.baylist.telegram.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ErrorHandler implements DialogHandler {


	// state ERROR
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			chatValue.setReplyText("у-у-упс. что-то пошло не так");
			chatValue.setState(State.DEFAULT);
		} else {
			if (chatValue.getInputText() != null && chatValue.getInputText().startsWith("/")) {
				chatValue.setReplyText("я не знаю такой команды :(");
				chatValue.setState(State.DEFAULT);
			}
		}
	}
}
