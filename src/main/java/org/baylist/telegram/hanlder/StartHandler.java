package org.baylist.telegram.hanlder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.UserService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartHandler implements DialogHandler {

	UserService userService;
	CommonResponseService responseService;


	// state START
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.START.getCallbackData())) {
				todoistAnswer(chatValue);
			} else if (callbackData.equals(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())) {
				if (userService.isExistToken(chatValue.getUser().getUserId())) {
					responseService.existToken(chatValue);
				} else {
					responseService.tokenRequest(chatValue);
				}
			} else if (callbackData.equals(Callbacks.START_1_TODOIST_TOKEN_CHANGE.getCallbackData())) {
				responseService.tokenRequest(chatValue);
			} else if (callbackData.equals(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())) {
				if (userService.isExistToken(chatValue.getUser().getUserId())) {
					friendsAnswer(chatValue);
				} else {
					responseService.doneWithouFriends(chatValue, State.START);
				}
			} else if (callbackData.equals(Callbacks.START_2_ADD_FRIENDS.getCallbackData())) {
				responseService.friendsRequest(chatValue, State.START);
			} else if (callbackData.equals(Callbacks.START_DONE.getCallbackData())) {
				done(chatValue);
			}

		} else {
			Update update = chatValue.getUpdate();
			if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(Commands.START.getCommand())) {
				responseService.start(chatValue);
			} else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 40) {
				responseService.tokenResponse(chatValue, true);
			} else if (update.hasMessage() && update.getMessage().hasContact()) {
				responseService.addFriend(chatValue, update);
			} else {
				error(chatValue);
			}
		}
	}

	private void friendsAnswer(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> второй этап настройки </b>
				добавить друзей, или ну их всех?
				
				<i>это можно будет сделать позже</i>
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("добавить")
								.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("та ну их")
								.callbackData(Callbacks.START_DONE.getCallbackData())
								.build())
				));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
	}

	private void done(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> настройка завершена </b>
				
				вы всегда можете попасть
				в главное меню командой:
				/menu
				""");
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.DEFAULT);
	}

	private void todoistAnswer(ChatValue chatValue) {
		chatValue.setReplyText("""
				⚙ первый этап настройки
				будете просто отправлять задачи друзьям 📩 или зарегистрируемся в <b>todoist</b>?
				
				без привязки к todoist можно только отправлять задачи тем, у кого он есть.
				
				<i>добавить токен todoist можно будет позже ⏳</i>
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("да (добавить/изменить токен)")
								.callbackData(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("пропустить пока")
								.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
								.build())
				));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
	}

	private static void error(ChatValue chatValue) {
		chatValue.setReplyText("не понимаю");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("давай всё заново")
						.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
						.build())
				));
		chatValue.setReplyKeyboard(markup);
	}


}
