package org.baylist.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.service.CommonResponseService;
import org.baylist.service.MenuService;
import org.baylist.service.TodoistService;
import org.baylist.service.UserService;
import org.baylist.telegram.handler.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DefaultHandler implements DialogHandler {

	TodoistService todoist;
	MenuService menuService;
	CommonResponseService responseService;
	UserService userService;
	Map<Long, String> inputTasksState = new ConcurrentHashMap<>();

	// state DEFAULT
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.SEND_TASK_TO.getCallbackData())) {
				long recipientId = Long.parseLong(callbackData.substring(Callbacks.SEND_TASK_TO.getCallbackData().length()));
				Long userId = chatValue.getUserId();
				todoist.sendTasksToTodoist(chatValue, userService.getUserFromDb(recipientId), inputTasksState.get(userId));
			} else if (callbackData.startsWith(Callbacks.VIEW_TASK_TO.getCallbackData())) {
				long userId = Long.parseLong(callbackData.substring(Callbacks.VIEW_TASK_TO.getCallbackData().length()));
				responseService.view(chatValue, userService.getUserFromDb(userId), false);
			} else {
				Callbacks callback = Callbacks.fromValue(chatValue.getCallbackData());
				switch (callback) {
					case VIEW -> responseService.checkAndView(chatValue, false);
					case FRIENDS_SETTINGS -> menuService.friendsSettings(chatValue, true);
					case MAIN_MENU -> menuService.mainMenu(chatValue, true);
					case DICT_SETTINGS -> menuService.dictionaryMainMenu(chatValue, true);
					case CANCEL -> responseService.cancel(chatValue, true);
				}
			}
		} else {
			String inputText = chatValue.getInputText();
			if (inputText.equals(Commands.DEFAULT.getCommand())) {
				responseService.cancel(chatValue, false);
			} else if (inputText.equals(Commands.DEFAULT_BOTTOM_KEYBOARD.getCommand())) {
				responseService.cancel(chatValue, false);
			} else if (inputText.equals(Commands.NOT_AI_BOTTOM_KEYBOARD.getCommand())) {
				chatValue.setReplyText("""
						режим аи отключён
						 включен режим приёма задач
						 можно вводить задачи по старинке""");
			} else {
				checkAndInput(chatValue);
			}
		}
	}

	public void checkAndInput(ChatValue chatValue) {
		String input = chatValue.getUpdate().getMessage().getText();
		if (validateInput(input)) {
			List<User> recipients = todoist.checkRecipients(chatValue);
			if (recipients.isEmpty()) {
				menuService.mainMenu(chatValue, false);
				chatValue.setReplyText("""
						никому не получится отправить задачи
						
						у тебя нет привязанного todoist, и тебя не добавили себе в друзья те у кого есть привязанный todoist
						
						держи главное меню. мб чем-то поможет. там справка есть.
						""");
			} else if (recipients.size() == 1) {
				todoist.sendTasksToTodoist(chatValue, recipients.getFirst(), input);
			} else {
				inputTasksState.put(chatValue.getUserId(), input);
				chatValue.setReplyText("выберите кому отправить задачки");
				chatValue.setReplyKeyboard(responseService.recipientsKeyboard(recipients, true));
			}
		} else {
			menuService.mainMenu(chatValue, false);
			chatValue.setReplyText("входящее сообщение не похоже было на список задачек, поэтому вот, держи главное меню");
		}
	}

	private boolean validateInput(String input) {
		return input.length() >= 2 &&
				Arrays.stream(Commands.values()).noneMatch(c -> input.contains(c.getCommand()));
		//вероятно в будущем тут будет добавлен ещё ряд условий
	}

}
