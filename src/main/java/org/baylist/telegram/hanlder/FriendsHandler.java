package org.baylist.telegram.hanlder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.MenuService;
import org.baylist.service.UserService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FriendsHandler implements DialogHandler {

	CommonResponseService responseService;
	UserService userService;
	MenuService menuService;

	// state FRIENDS
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.FRIEND_REMOVE_MY_CHOICE.getCallbackData())) {
				userService.removeMyFriend(chatValue);
			} else if (callbackData.startsWith(Callbacks.FRIEND_REMOVE_FROM_CHOICE.getCallbackData())) {
				userService.removeFromFriend(chatValue);
			} else {
				Callbacks callback = Callbacks.fromValue(callbackData);
				switch (callback) {
					case MAIN_MENU -> menuService.mainMenu(chatValue, true);
					case CANCEL -> responseService.cancel(chatValue, true);
					case FRIENDS_SETTINGS, START_DONE -> menuService.friendsSettings(chatValue, true);
					case MY_FRIENDS -> responseService.listMyFriends(chatValue);
					case FRIENDS_ME -> responseService.listFriendsMe(chatValue);
					case FRIENDS_HELP -> responseService.friendsHelp(chatValue);
					case START_2_ADD_FRIENDS -> {
						if (userService.isExistToken(chatValue.getUserId())) {
							responseService.friendsRequest(chatValue, State.FRIENDS);
						} else {
							responseService.doneWithouFriends(chatValue, State.FRIENDS);
						}
					}
					case REMOVE_MY_FRIEND -> userService.removeMyFriendsList(chatValue);
					case REMOVE_FROM_FRIEND -> userService.removeFromFriendsList(chatValue);
				}
			}
		} else {
			Update update = chatValue.getUpdate();
			if (update.hasMessage() && update.getMessage().hasContact()) {
				responseService.addFriend(chatValue, update);
			} else {
				menuService.friendsSettings(chatValue, false);
			}
		}
	}

}
