package org.baylist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuService {

	public void mainMenu(ChatValue chatValue, boolean isEdit) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("посмотреть текущие задачи")
						.callbackData(Callbacks.VIEW.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("настройка словарика")
						.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("настройка дружбы")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("настройка уведомлений")
						.callbackData(Callbacks.NOTIFY_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("настройка todoist")
						.callbackData(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("сводная информация")
						.callbackData(Callbacks.INFO.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("фсё, пока хватит")
						.callbackData(Callbacks.CANCEL.getCallbackData())
						.build())
		));

		if (isEdit) {
			chatValue.setEditText("главное меню");
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("главное меню");
			chatValue.setReplyKeyboard(markup);
		}

		chatValue.setState(State.MENU);
	}


}

