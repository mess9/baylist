package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Commands {

	START("/start", "первоначальная настройка"),
	CLEAR("/clear", "очистить список покупок"),
	VIEW("/view", "посмотреть текущий список покупок"),
	REPORT("/report", "обратная связь, донаты, пожелания автору"),
	DICTIONARY("/dictionary", "редактировать словарик"),
	DEFAULT("/default", "режим ввода задач"),
	HELP("/help", "описание функционала бота, примеры использования"),
	MENU("/menu", "главное меню бота"),
	NOT_COMMAND("is_not_command", "for error handler"),
	;

    private final String command;
	private final String description;

	public static Commands fromValue(String command) {
		return Arrays.stream(Commands.values())
				.filter(c -> c.getCommand().equals(command))
				.findFirst()
				.orElse(NOT_COMMAND);
	}

}
