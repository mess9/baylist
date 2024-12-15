package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatState;
import org.baylist.dto.telegram.Commands;
import org.baylist.service.TodoistService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class Command {

	private TodoistService todoist;

	public void commandHandler(ChatState chatState) {
		String updateMessage = chatState.getUpdate().getMessage().getText();
		if (Arrays.stream(Commands.values()).anyMatch(c -> c.getCommand().equals(updateMessage))) {
			if (updateMessage.equals(Commands.CLEAR.getCommand())) {
				clear(chatState);
			} else if (updateMessage.equals(Commands.VIEW.getCommand())) {
				view(chatState);
			} else if (updateMessage.equals(Commands.SYNC.getCommand())) {
				sync(chatState);
			} else if (updateMessage.equals(Commands.START.getCommand())) {
				start(chatState);
			}
		}
	}

	private void clear(ChatState chatState) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("СЖЕЧЬ ИХ ВСЕХ!")
						.callbackData(Callbacks.APPROVE.getCallbackData())
						.build(),
						InlineKeyboardButton.builder()
								.text("неа, не надо")
								.callbackData(Callbacks.CANCEL.getCallbackData())
								.build())));
		chatState.getMessage().setText("удалить вообще все незакрытые задачи?");
		chatState.getMessage().setReplyMarkup(markup);
	}

	private void view(ChatState chatState) {
		chatState.getMessage().setText(todoist.getBuylistProject());
		chatState.getMessage().setParseMode("html");
		chatState.setCommandIsProcess(true);
	}

	private void sync(ChatState chatState) {
		todoist.syncBuyListData();
		chatState.getMessage().setText("данные синхронизированы с todoist");
		chatState.setCommandIsProcess(true);
	}

	private void start(ChatState chatState) {
		chatState.getMessage().setText("""
				yay!, приветствую.
				этот бот написал филом, что бы отправлять ему список покупок
				
				если вы не знаете кто такой фил, вы явно обратились к этому боту по ошибке,
				но всё равно будьте вы счастливы, но только если вы хороший человек,
				если не хороший, например путин или что-то похожее, то пожалуйста убей себя каким нибудь болезненным и публичным способом.
				 ня)
				с любовью. фил.
				""");
		chatState.setCommandIsProcess(true);
	}
}
