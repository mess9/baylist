package org.baylist.telegram2.hanlder.config;

import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;

@Component
public class CommandChecker {

	public void checkCommandInput(ChatValue chatValue) {
		if (!chatValue.isCallback()) {
			Commands input = Commands.fromValue(chatValue.getInputText());
			switch (input) {
				case Commands.START -> chatValue.setState(State.START);
				case Commands.CLEAR -> chatValue.setState(State.CLEAR);
				case Commands.VIEW -> chatValue.setState(State.VIEW);
				case Commands.REPORT -> chatValue.setState(State.REPORT);
			}
		}
	}

}
