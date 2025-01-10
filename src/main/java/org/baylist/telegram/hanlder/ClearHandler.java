package org.baylist.telegram.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.TodoistService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@AllArgsConstructor
public class ClearHandler implements DialogHandler {

	private TodoistService todoist;
	private CommonResponseService commonResponseService;

	// state CLEAR
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.APPROVE.getCallbackData())) {
				chatValue.setReplyText(todoist.clearBuyList());
			} else if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				commonResponseService.cancelMessage(chatValue);
			}
			chatValue.setState(State.DEFAULT);
		} else {
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
					new InlineKeyboardRow(
							InlineKeyboardButton.builder()
									.text("СЖЕЧЬ ИХ ВСЕХ!")
									.callbackData(Callbacks.APPROVE.getCallbackData())
									.build(),
							InlineKeyboardButton.builder()
									.text("неа, не надо")
									.callbackData(Callbacks.CANCEL.getCallbackData())
									.build())));
			chatValue.setReplyText("удалить вообще все незакрытые задачи?");
			chatValue.setReplyKeyboard(markup);
			chatValue.setState(State.CLEAR);
		}
	}
}
