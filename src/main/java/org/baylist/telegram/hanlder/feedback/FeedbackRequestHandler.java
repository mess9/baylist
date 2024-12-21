package org.baylist.telegram.hanlder.feedback;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@AllArgsConstructor
public class FeedbackRequestHandler implements DialogHandler {

	// state FEEDBACK_REQUEST
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.DONATE.getCallbackData())) {
				chatValue.setReplyText("""
						спасибо за нажатие на эту кнопку!
						
						приму любого размера помощь (до 1G$)
						💳 (mastercard)
						4454 3000 0304 4598
						₿ (bitcoin)
						bc1qdgnwxpjtfhqztw6thq3yukcddrpms48wk4dhy0
						""");
				chatValue.setState(State.DEFAULT);
			} else if (callbackData.equals(Callbacks.FEEDBACK.getCallbackData())) {
				chatValue.setReplyText("я вас внимательно слушаю");
				chatValue.setState(State.FEEDBACK_ANSWER);
			}
		} else {
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
					new InlineKeyboardRow(
							InlineKeyboardButton.builder()
									.text("take my money!")
									.callbackData(Callbacks.DONATE.getCallbackData())
									.build(),
							InlineKeyboardButton.builder()
									.text("feedback")
									.callbackData(Callbacks.FEEDBACK.getCallbackData())
									.build())));
			chatValue.setReplyText("""
					тут можно оставить обратную связь по работе данного бота
					принимаются:
					 - донаты
					 - баг репорты
					 - слова благодарности
					 - конструктивная критика
					 - смешные мемы
					""");
			chatValue.setReplyKeyboard(markup);
			chatValue.setState(State.FEEDBACK_REQUEST);
		}
	}
}

