package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Callbacks {

	APPROVE("approve"),
	CANCEL("галя отмена"),
	VIEW("view"),
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
	CATEGORY_CHOICE("category:"),
	REMOVE_CATEGORY("removeCategory"),
	REMOVE_TASK_TO_CATEGORY("removeTasksToCategory"),//todo
	VIEW_CATEGORIES("viewCategories"),
	FEEDBACK("feedback"),
	;


	private final String callbackData;

	public static Callbacks fromValue(String callbackData) {
		return Arrays.stream(Callbacks.values())
				.filter(c -> c.getCallbackData().equals(callbackData))
				.findFirst()
				.orElseThrow();
	}

}
