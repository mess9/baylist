package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.service.TodoistService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.baylist.util.log.TgLog.inputLogButton;

@Component
@AllArgsConstructor
public class Button {

	private TodoistService todoist;

	//ограничения полученные опытным путём
	// максимальное количество кнопок в одной строке - 8
	// - в название влезает 4 символа
	// - 3 символа если с телефона
	// максимальное в высоту - хз, пробовал 20, 20 влезает.
	// - в название влезает 56 символов
	// - 41 символ если с телефона

	public SendMessage buttons(Update update) {
		String data = update.getCallbackQuery().getData();
		SendMessage message = SendMessage.builder().text("").chatId(update.getCallbackQuery().getMessage().getChatId()).build();
		inputLogButton(update);

		if (data.equals(Callbacks.CANCEL.getCallbackData())) {
			cancel(message);
		} else if (data.equals(Callbacks.APPROVE.getCallbackData())) {
			approve(message);
		}

		return message;
	}

	private void cancel(SendMessage message) {
		message.setText("ну ошибся, бывает, со всеми случается, не переживай ты так");
	}

	private void approve(SendMessage message) {
		message.setText(todoist.clearBuyList());
	}
}

