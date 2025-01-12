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
						.text("справка")
						.callbackData(Callbacks.HELP.getCallbackData())
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

	public void friendsSettings(ChatValue chatValue, boolean isEdit) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("мои друзья")
						.callbackData(Callbacks.MY_FRIENDS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("у кого я в друзьях")
						.callbackData(Callbacks.FRIENDS_ME.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("справка по друзьям")
						.callbackData(Callbacks.FRIENDS_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("добавить друга")
						.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("удалить друга :(")
						.callbackData(Callbacks.REMOVE_FRIEND.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("главное меню")
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("фсё, хватит пока")
						.callbackData(Callbacks.CANCEL.getCallbackData())
						.build())
		));

		if (isEdit) {
			chatValue.setEditText("настройка дружбы");
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("настройка дружбы");
			chatValue.setReplyKeyboard(markup);
		}
		chatValue.setState(State.FRIENDS);
	}

	public void dictionaryMainMenu(ChatValue chatValue, boolean isEdit) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("показать словарик")
						.callbackData(Callbacks.DICT_VIEW.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("новая категория")
						.callbackData(Callbacks.DICT_ADD_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("добавить варианты в категорию")
						.callbackData(Callbacks.DICT_ADD_TASKS_TO_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("переименовать категорию")
						.callbackData(Callbacks.DICT_RENAME_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("удалить категории")
						.callbackData(Callbacks.DICT_REMOVE_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("удалить варианты задач")
						.callbackData(Callbacks.DICT_REMOVE_VARIANT.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("справка по словарику")
						.callbackData(Callbacks.DICT_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("главное меню")
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("фсё, пока хватит")
						.callbackData(Callbacks.CANCEL.getCallbackData())
						.build())
		));
		if (isEdit) {
			chatValue.setEditText("настройки словарика");
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("настройки словарика");
			chatValue.setReplyKeyboard(markup);
		}

		chatValue.setState(State.DICT_SETTING);
	}


}

