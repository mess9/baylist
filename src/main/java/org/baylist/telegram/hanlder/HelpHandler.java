package org.baylist.telegram.hanlder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HelpHandler implements DialogHandler {

	MenuService menuService;
	CommonResponseService responseService;

	@Override
	public void handle(ChatValue chatValue) {

		if (chatValue.isCallback()) {
			Callbacks callback = Callbacks.fromValue(chatValue.getCallbackData());
			switch (callback) {
				case MAIN_MENU -> responseService.cancel(chatValue, true);
				case FRIENDS_HELP -> responseService.friendsHelp(chatValue);
				case DICT_HELP -> responseService.dictHelp(chatValue);
				case BOT_HELP -> responseService.botHelp(chatValue);
				case TODOIST_HELP -> responseService.todoistHelp(chatValue);
				case FRIENDS_SETTINGS, DICT_SETTINGS, HELP -> menuService.help(chatValue, true);
				default -> chatValue.setState(State.ERROR);
			}
		} else {
			menuService.help(chatValue, false);
		}
	}
}
