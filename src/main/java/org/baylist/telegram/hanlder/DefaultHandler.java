package org.baylist.telegram.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.TodoistService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultHandler implements DialogHandler {

	private TodoistService todoist;

	// state DEFAULT
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			if (chatValue.getCallbackData().equals(Callbacks.VIEW.getCallbackData())) {
				chatValue.setReplyText(todoist.getBuylistProject());
				chatValue.setReplyParseModeHtml();
			}
		} else {
			if (todoist.storageIsEmpty()) {
				todoist.syncBuyListData();
			}
			todoist.sendTasksToTodoist(chatValue);
		}
		chatValue.setState(State.DEFAULT);
	}
}
