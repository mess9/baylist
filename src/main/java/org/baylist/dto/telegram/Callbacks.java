package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Callbacks {

	START("start"),
	START_DONE("startDone"),
	TODOIST_TOKEN("todoistToken"),
	WITHOUT_TODOIST_TOKEN("withoutTodoistToken"),
	ADD_FRIENDS("addFriends"),
	NO_FRIENDS("noFriends"),

	APPROVE("approve"),
	CANCEL("галя отмена"),

	VIEW("view"),

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

	;


	private final String callbackData;

	public static Callbacks fromValue(String callbackData) {
		return Arrays.stream(Callbacks.values())
				.filter(c -> c.getCallbackData().equals(callbackData))
				.findFirst()
				.orElseThrow();
	}

}
