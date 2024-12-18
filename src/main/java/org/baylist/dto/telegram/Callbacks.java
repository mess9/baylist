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
	ADD_TASK_TO_CATEGORY("addTaskToCategory"),
	FEEDBACK("feedback");


	private final String callbackData;

}
