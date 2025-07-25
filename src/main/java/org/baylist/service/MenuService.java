package org.baylist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuService {

	String invisibleSpace = "⠀";

	public void defaultMenu(ChatValue chatValue) {
		if (chatValue.getMessage() != null && chatValue.getMessage().getReplyMarkup() == null && !chatValue.getState().equals(State.START)) {
			String textInputMode;
			if (chatValue.getState().equals(State.AI)) {
				textInputMode = Commands.NOT_AI_BOTTOM_KEYBOARD.getCommand();
			} else {
				textInputMode = Commands.DEFAULT_BOTTOM_KEYBOARD.getCommand();
			}
				ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
						List.of(new KeyboardRow(List.of(
								KeyboardButton.builder().text(textInputMode).build(),
								KeyboardButton.builder().text(Commands.MENU_BOTTOM_KEYBOARD.getCommand()).build()
						))), true, false, true, " =^..^= кнопотьки =^..^= ", true
				);
				chatValue.setReplyKeyboard(markup);
			}
	}

	public void friendsSettings(ChatValue chatValue, boolean isEdit) {
		String invisibleSpace = "⠀";
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83E\uDD17 мои друзья" + invisibleSpace.repeat(9))
						.callbackData(Callbacks.MY_FRIENDS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDC65 у кого я в друзьях" + invisibleSpace.repeat(4))
						.callbackData(Callbacks.FRIENDS_ME.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDDE3 справка по друзьям" + invisibleSpace.repeat(2))
						.callbackData(Callbacks.FRIENDS_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("➕ добавить друга" + invisibleSpace.repeat(5))
						.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDEAB удалить друга :(" + invisibleSpace.repeat(5))
						.callbackData(Callbacks.REMOVE_MY_FRIEND.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83E\uDDF9 удалиться из друзей" + invisibleSpace.repeat(1))
						.callbackData(Callbacks.REMOVE_FROM_FRIEND.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83C\uDFE0 главное меню" + invisibleSpace.repeat(6))
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCF2 фсё, хватит пока" + invisibleSpace.repeat(4))
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
						.text("\uD83D\uDD0D показать словарик" + invisibleSpace.repeat(11))
						.callbackData(Callbacks.DICT_VIEW.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83C\uDFF7 новая категория" + invisibleSpace.repeat(13))
						.callbackData(Callbacks.DICT_ADD_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCCB добавить варианты в категорию" + invisibleSpace.repeat(1))
						.callbackData(Callbacks.DICT_ADD_TASKS_TO_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("✏ переименовать категорию" + invisibleSpace.repeat(5))
						.callbackData(Callbacks.DICT_RENAME_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDDD1 удалить категории" + invisibleSpace.repeat(11))
						.callbackData(Callbacks.DICT_REMOVE_CATEGORY.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("✂ удалить варианты задач" + invisibleSpace.repeat(7))
						.callbackData(Callbacks.DICT_REMOVE_VARIANT.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCD6 справка по словарику" + invisibleSpace.repeat(9))
						.callbackData(Callbacks.DICT_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83C\uDFE0 главное меню" + invisibleSpace.repeat(15))
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCF2 фсё, пока хватит" + invisibleSpace.repeat(14))
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

	public void notifySettings(ChatValue chatValue) {
		mainMenu(chatValue, true);
		chatValue.setEditText("""
				данный функционал пока отсутствует
				разработчик разрабатывает, но тут ещё не всё разработал
				/menu
				/default
				""");
	}

	public void mainMenu(ChatValue chatValue, boolean isEdit) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDC40 текущие задачи" + invisibleSpace.repeat(6))
						.callbackData(Callbacks.VIEW.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCDA настройка словарика" + invisibleSpace.repeat(3))
						.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("⚙ настройка дружбы" + invisibleSpace.repeat(5))
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDD14 настройка уведомлений" + invisibleSpace.repeat(1))
						.callbackData(Callbacks.NOTIFY_SETTINGS.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDD17 настройка todoist" + invisibleSpace.repeat(6))
						.callbackData(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCCA сводная информация" + invisibleSpace.repeat(3))
						.callbackData(Callbacks.INFO.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("❓ справка" + invisibleSpace.repeat(13))
						.callbackData(Callbacks.HELP.getCallbackData())
						.build()),
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("\uD83E\uDD16⠀⠀⠀нейробалбес" + invisibleSpace.repeat(7))
//						.callbackData(Callbacks.AI.getCallbackData())
//						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCF2 фсё, пока хватит" + invisibleSpace.repeat(7))
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

	public void help(ChatValue chatValue, boolean isEdit) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDC65 справка про друзей")
						.callbackData(Callbacks.FRIENDS_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCD6 справка про словарик")
						.callbackData(Callbacks.DICT_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("❔ справка по работе с ботом")
						.callbackData(Callbacks.BOT_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDCCD почему todoist")
						.callbackData(Callbacks.TODOIST_HELP.getCallbackData())
						.build()),
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("\uD83D\uDD19 назад")
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build())
		));
		String text = "если нужна ещё помощь по механизмам взаимодействия с ботом - прошу сообщить через /report";
		if (isEdit) {
			chatValue.setEditText(text);
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyText(text);
			chatValue.setReplyKeyboard(markup);
		}

	}
}

