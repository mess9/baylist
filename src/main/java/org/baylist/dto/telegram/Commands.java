package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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
	HELP("/help"),
	NOT_COMMAND("not_command"),
    ;

    private final String command;

	public static Commands fromValue(String command) {
		return Arrays.stream(Commands.values())
				.filter(c -> c.getCommand().equals(command))
				.findFirst()
				.orElse(NOT_COMMAND);

	}
}
