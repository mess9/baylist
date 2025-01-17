package org.baylist.telegram.hanlder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.MenuService;
import org.baylist.service.UserService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MainMenuHandler implements DialogHandler {

	MenuService menuService;
	UserService userService;
	CommonResponseService responseService;

	// state MENU
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.VIEW_TASK_TO.getCallbackData())) {
				Long userId = Long.valueOf(callbackData.substring(Callbacks.VIEW_TASK_TO.getCallbackData().length()));
				responseService.view(chatValue, userService.getUserFromDb(userId), true);
			} else {
				Callbacks callback = Callbacks.fromValue(chatValue.getCallbackData());
				switch (callback) {
					case CANCEL -> responseService.cancel(chatValue);
					case MAIN_MENU, START_2_FRIENDS_REQUEST -> menuService.mainMenu(chatValue, true);
					case DICT_SETTINGS -> menuService.dictionaryMainMenu(chatValue, true);
					//change token
					case START_1_TODOIST_TOKEN_REQUEST -> {
						if (userService.isExistToken(chatValue.getUser().getUserId())) {
							responseService.existToken(chatValue);
						} else {
							responseService.tokenRequest(chatValue);
						}
					}
					case START_1_TODOIST_TOKEN_CHANGE -> responseService.tokenRequest(chatValue);
					case FRIENDS_SETTINGS -> menuService.friendsSettings(chatValue, true);
					case NOTIFY_SETTINGS -> menuService.notifySettings(chatValue); //todo notifications
					case HELP -> menuService.help(chatValue);
					case FRIENDS_HELP -> responseService.friendsHelp(chatValue);
					case DICT_HELP -> responseService.dictHelp(chatValue);
					case VIEW -> responseService.checkAndView(chatValue, true);
					case INFO -> responseService.info(chatValue);
					default -> chatValue.setState(State.ERROR);
				}
			}
		} else {
			Update update = chatValue.getUpdate();
			if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(Commands.MENU.getCommand())) {
				menuService.mainMenu(chatValue, false);
			} else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 40) {
				responseService.tokenResponse(chatValue, false);
			} else {
				menuService.mainMenu(chatValue, false);
			}
		}
	}


}
