package org.baylist.telegram.hanlder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.InputTaskState;
import org.baylist.dto.telegram.State;
import org.baylist.service.MenuService;
import org.baylist.service.TodoistService;
import org.baylist.telegram.hanlder.config.DialogHandler;
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
	Map<Long, InputTaskState> inputTaskStateMap = new ConcurrentHashMap<>();

	// state DEFAULT
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.VIEW.getCallbackData())) {
				chatValue.setReplyText(todoist.getBuylistProject());
				chatValue.setReplyParseModeHtml();
			} else if (callbackData.equals(Callbacks.FRIENDS_SETTINGS.getCallbackData())) {
				menuService.friendsSettings(chatValue, true);
			} else if (callbackData.equals(Callbacks.MAIN_MENU.getCallbackData())) {
				menuService.mainMenu(chatValue, true);
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
			}
		} else {
			if (todoist.storageIsEmpty()) {
				todoist.syncBuyListData();
			}
			todoist.sendTasksToTodoist(chatValue);
			chatValue.setState(State.DEFAULT);
		}
	}

	private void checkInput(ChatValue chatValue) {
		if (validateInput(chatValue.getUpdate().getMessage().getText())) {
			List<User> recipients = todoist.checkRecipients(chatValue);
			if (recipients.isEmpty()) {
				menuService.mainMenu(chatValue, false);
				chatValue.setReplyText("""
						никому не получится отправить задачи
						
						у тебя нет привязанного todoist, и тебя не добавили себе в друзья те у кого есть привязанный todoist
						
						держи главное меню. мб чем-то поможет. там справка есть.
						""");
			} else if (recipients.size() == 1) {
				todoist.sendTasksToTodoist(chatValue/*, recipients.getFirst()*/);
			} else {

			}
		} else {
			menuService.mainMenu(chatValue, false);
			chatValue.setReplyText("входящее сообщение не похоже было на список задачек, поэтому вот, держи главное меню");
		}
	}


	private boolean validateInput(String input) {
		return input.length() > 3 &&
				Arrays.stream(Commands.values()).noneMatch(c -> input.contains(c.getCommand()));
		//вероятно в будущем тут будет добавлен ещё ряд условий
	}

}
