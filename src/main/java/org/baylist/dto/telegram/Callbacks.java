package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Callbacks {

	APPROVE("approve"),
	CANCEL("галя отмена"),
	;


	private final String callbackData;

}
