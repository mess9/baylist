package org.baylist.telegram.hanlder.config;

import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
public class CommandChecker {

	public void checkCommandInput(ChatValue chatValue) {
		if (!chatValue.isCallback() && chatValue.getInputText().startsWith("/")) {
			Commands input = Commands.fromValue(chatValue.getInputText());
			switch (input) {
				case Commands.START -> chatValue.setState(State.START);
				case Commands.CLEAR -> chatValue.setState(State.CLEAR);
				case Commands.VIEW -> chatValue.setState(State.VIEW);
				case Commands.REPORT -> chatValue.setState(State.FEEDBACK_REQUEST);
				case Commands.DICTIONARY -> chatValue.setState(State.DICT_SETTING);
				case Commands.DEFAULT -> chatValue.setState(State.DEFAULT);
				case Commands.HELP -> chatValue.setState(State.HELP); //todo сделать хелп
				case Commands.MENU -> chatValue.setState(State.MENU);
				default -> chatValue.setState(State.ERROR);
			}
		}
	}


	public void setCommandList(TelegramClient telegramClient) {
		try {
			telegramClient.execute(new SetMyCommands(List.of(
					new BotCommand(Commands.MENU.getCommand(), Commands.MENU.getDescription()),
					new BotCommand(Commands.VIEW.getCommand(), Commands.VIEW.getDescription()),
					new BotCommand(Commands.CLEAR.getCommand(), Commands.CLEAR.getDescription()),
					new BotCommand(Commands.DICTIONARY.getCommand(), Commands.DICTIONARY.getDescription()),
					new BotCommand(Commands.DEFAULT.getCommand(), Commands.DEFAULT.getDescription()),
					new BotCommand(Commands.HELP.getCommand(), Commands.HELP.getDescription()),
					new BotCommand(Commands.START.getCommand(), Commands.START.getDescription()),
					new BotCommand(Commands.REPORT.getCommand(), Commands.REPORT.getDescription())
			)));
		} catch (TelegramApiException e) {
			log.error(e.getMessage());
		}
	}

}
