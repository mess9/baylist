package org.baylist.telegram2.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.TodoistService;
import org.baylist.telegram2.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultHandler implements DialogHandler {

	private TodoistService todoist;

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
