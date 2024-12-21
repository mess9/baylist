package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Callbacks {

	APPROVE("approve"),
	CANCEL("галя отмена"),
	VIEW("view"),
	DONATE("donate"),
	ADD_CATEGORY("addCategory"),
	ADD_TASKS_TO_CATEGORY("addTasksToCategory"),
	CATEGORY_CHOICE("category:"),
	REMOVE_CATEGORY("removeCategory"), //todo
	REMOVE_TASK_TO_CATEGORY("removeTasksToCategory"),//todo
	VIEW_CATEGORIES("viewCategories"), //todo просматривать и удалять содержимое словарика
	FEEDBACK("feedback");


	private final String callbackData;

}
