package org.baylist.dto.telegram;

import lombok.Getter;

@Getter
public enum State {

	START,
	DEFAULT,
	CLEAR,
	VIEW,
	SYNC,
	HELP,
	DICT_SETTING,
	DICT_ADD_CATEGORY,
	DICT_ADD_TASK_TO_CATEGORY,
	FEEDBACK_REQUEST,
	FEEDBACK_ANSWER,
	MENU,
	ERROR,


}
