package org.baylist.telegram.hanlder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.MenuService;
import org.baylist.service.TodoistService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DefaultHandler implements DialogHandler {

	TodoistService todoist;
	MenuService menuService;


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
}
