package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Callbacks {

	START("start"),
	START_DONE("startDone"),
	START_1_TODOIST_TOKEN_REQUEST("todoistToken"),
	START_1_TODOIST_TOKEN_CHANGE("todoistTokenChange"),
	START_2_FRIENDS_REQUEST("friendsRequest"),
	START_2_ADD_FRIENDS("addFriends"),

	APPROVE("approve"),
	CANCEL("галя отмена"),

	VIEW("view"),
	INFO("info"),
	MAIN_MENU("mainMenu"),
	HELP("help"),

	FEEDBACK("feedback"),
	DONATE("donate"),

	SEND_TASK_TO("sendTaskTo:"),
	VIEW_TASK_TO("viewTaskTo:"),

	DICT_SETTINGS("dictSettings"),
	DICT_VIEW("dictView"),
	DICT_VIEW_PAGINATION_BACK("dictViewPaginationBack"),
	DICT_VIEW_PAGINATION_FORWARD("dictViewPaginationForward"),
	DICT_HELP("dictHelp"),
	DICT_ADD_CATEGORY("dictAddCategory"),
	DICT_ADD_TASKS_TO_CATEGORY("dictAddTasksToCategory"),
	DICT_REMOVE_CATEGORY("dictRemoveCategory"),
	DICT_RENAME_CATEGORY("dictRenameCategory"),
	DICT_REMOVE_VARIANT("dictRemoveVariant"),
	CATEGORY_CHOICE("category:"),
	REMOVE_CATEGORY("removeCategory"),

	FRIENDS_SETTINGS("friendsSettings"),
	FRIENDS_HELP("friendsHelp"),
	MY_FRIENDS("myFriends"),
	FRIENDS_ME("friendsMe"),
	REMOVE_MY_FRIEND("removeFriend"),
	REMOVE_FROM_FRIEND("removeFromFriend"),
	FRIEND_REMOVE_MY_CHOICE("friendMy:"),
	FRIEND_REMOVE_FROM_CHOICE("friendFrom:"),

	NOTIFY_SETTINGS("notifySettings"),
	TODOIST_SETTINGS("todoistSettings"),
	BOT_HELP("botHelp"),
	TODOIST_HELP("todoistHelp"),

	AI("ai")

	;


	private final String callbackData;

	public static Callbacks fromValue(String callbackData) {
		return Arrays.stream(Callbacks.values())
				.filter(c -> c.getCallbackData().equals(callbackData))
				.findAny()
				.orElseThrow();
	}

}
