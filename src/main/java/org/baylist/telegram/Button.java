package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatState;
import org.baylist.service.TodoistService;
import org.springframework.stereotype.Component;

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

	public void buttons(ChatState chatState) {
		String data = chatState.getUpdate().getCallbackQuery().getData();
		inputLogButton(chatState.getUpdate());

		if (data.equals(Callbacks.CANCEL.getCallbackData())) {
			cancel(chatState);
		} else if (data.equals(Callbacks.APPROVE.getCallbackData())) {
			approve(chatState);
		} else if (data.equals(Callbacks.VIEW.getCallbackData())) {
			view(chatState);
		} else if (data.equals(Callbacks.DONATE.getCallbackData())) {
			donate(chatState);
		} else if (data.equals(Callbacks.FEEDBACK.getCallbackData())) {
			feedback(chatState);
		}
	}

	private void cancel(ChatState chatState) {
		chatState.getMessage().setText("ну ошибся, бывает, со всеми случается, не переживай ты так");
	}

	private void approve(ChatState chatState) {
		chatState.getMessage().setText(todoist.clearBuyList());
	}

	private void view(ChatState chatState) {
		chatState.getMessage().setText(todoist.getBuylistProject());
		chatState.getMessage().setParseMode("html");
	}

	private void donate(ChatState chatState) {
		chatState.getMessage().setText("""
				спасибо за нажатие на эту кнопку!
				
				приму любого размера помощь (до 1G$)
				💳 (mastercard)
				4454 3000 0304 4598
				₿ (bitcoin)
				bc1qdgnwxpjtfhqztw6thq3yukcddrpms48wk4dhy0
				""");
	}

	private void feedback(ChatState chatState) {
		chatState.getMessage().setText("я вас внимательно слушаю");
	}
}

