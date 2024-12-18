package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Commands {

    START("/start"),
    CLEAR("/clear"),
    VIEW("/view"),
    SYNC("/sync"),
	ADD_CATEGORY("/add_category"),
	LINK_CATEGORY_TO_TASK("/link_category_to_task"),
	REPORT("/report"),
	HELP("/help")
    ;

    private final String command;
}
