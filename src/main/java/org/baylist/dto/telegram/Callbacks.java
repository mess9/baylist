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

	FEEDBACK("feedback"),
	DONATE("donate"),

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
	NOTIFY_SETTINGS("notifySettings"),
	TODOIST_SETTINGS("todoistSettings"),


	;


	private final String callbackData;

	public static Callbacks fromValue(String callbackData) {
		return Arrays.stream(Callbacks.values())
				.filter(c -> c.getCallbackData().equals(callbackData))
				.findFirst()
				.orElseThrow();
	}

}
