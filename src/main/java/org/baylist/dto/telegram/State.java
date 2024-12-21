package org.baylist.dto.telegram;

import lombok.Getter;

@Getter
public enum State {

	START,
	DEFAULT,
	CLEAR,
	VIEW,
	REPORT,
	SYNC,
	ADD_CATEGORY,
	ADD_TASK_TO_CATEGORY,
	LINK_CATEGORY_TO_TASK,
	FEEDBACK;

}
