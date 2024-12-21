package org.baylist.telegram2.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.TodoistService;
import org.baylist.telegram2.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewHandler implements DialogHandler {

	private TodoistService todoist;

	@Override
	public void handle(ChatValue chatValue) {
		chatValue.setReplyText(todoist.getBuylistProject());
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.DEFAULT);
	}
}
