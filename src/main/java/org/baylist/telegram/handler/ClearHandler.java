package org.baylist.telegram.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.TodoistService;
import org.baylist.service.UserService;
import org.baylist.telegram.handler.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClearHandler implements DialogHandler {

	TodoistService todoist;
	CommonResponseService responseService;
	UserService userService;

	// state CLEAR
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.APPROVE.getCallbackData())) {
				chatValue.setReplyText(todoist.clearBuyList(chatValue));
			} else if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			}
			chatValue.setState(State.DEFAULT);
		} else {
			if (userService.isExistToken(chatValue.getUserId())) {
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
				chatValue.setReplyText("удалить вообще все не закрытые задачи из todoist?");
				chatValue.setReplyKeyboard(markup);
				chatValue.setState(State.CLEAR);
			} else {
				chatValue.setReplyText("не имея своего списка задач, не надо и пытаться что-то там удалить");
				chatValue.setState(State.DEFAULT);
			}
		}
	}
}
