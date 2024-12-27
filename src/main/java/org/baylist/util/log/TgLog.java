package org.baylist.util.log;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.baylist.util.log.LogUtil.reduceEmptyLines;

@Slf4j
public class TgLog {

	private static final int QUANTITY_CHAR_TO_CUT_MESSAGE = 150;

	public static SendMessage outputLog(SendMessage message) {
		String chatId = message.getChatId();
		String text = message.getText();
		text = reduceEmptyLines(text);
		if (text.length() > QUANTITY_CHAR_TO_CUT_MESSAGE) {
			text = text.substring(0, QUANTITY_CHAR_TO_CUT_MESSAGE) + "...";
		}
		log.info(" -> Answer to chat id - {}, \n Text - {}",
				chatId,
				text);
		return message;
	}

	public static EditMessageText outputLog(EditMessageText message) {
		String chatId = message.getChatId();
		String text = message.getText();
		text = reduceEmptyLines(text);
		if (text.length() > QUANTITY_CHAR_TO_CUT_MESSAGE) {
			text = text.substring(0, QUANTITY_CHAR_TO_CUT_MESSAGE) + "...";
		}
		log.info(" -> Edit answer to chat id - {}, \n Text - {}",
				chatId,
				text);
		return message;
	}

	public static void inputLog(Update update) {
		if (update.hasMessage()) {
			inputLogMessage(update);
		} else if (update.hasCallbackQuery()) {
			inputLogButton(update);
		}
	}

	private static void inputLogMessage(Update update) {
	    String user_first_name = update.getMessage().getFrom().getFirstName();
	    String user_last_name = update.getMessage().getFrom().getLastName();
	    long user_id = update.getMessage().getFrom().getId();
        String answer = update.getMessage().getText();

        log.info(" -> Message from {} {} (id = {}) \n Text - {}",
                user_first_name,
                user_last_name,
                user_id,
                answer);
    }

	private static void inputLogButton(Update update) {
        String user_first_name = update.getCallbackQuery().getMessage().getChat().getFirstName();
        String user_last_name = update.getCallbackQuery().getMessage().getChat().getLastName();
        long user_id = update.getCallbackQuery().getMessage().getChat().getId();
        String data = update.getCallbackQuery().getData();

        log.info(" -> Callback from {} {} (id = {}) \n Data - {}",
                user_first_name,
                user_last_name,
                user_id,
                data);
    }


}
