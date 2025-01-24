package org.baylist.util.log;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.baylist.util.log.LogUtil.reduceEmptyLines;

@Slf4j
public class TgLog {

	private static final int QUANTITY_CHAR_TO_CUT_MESSAGE = 150000;

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
		if (update.hasMessage() && update.getMessage().hasText()) {
			inputLogMessage(update);
		} else if (update.hasCallbackQuery()) {
			inputLogButton(update);
		} else if (update.hasMessage() && update.getMessage().hasContact()) {
			inputLogContact(update);
		}
	}

	private static void inputLogMessage(Update update) {
	    String user_first_name = update.getMessage().getFrom().getFirstName();
	    String user_last_name = update.getMessage().getFrom().getLastName();
	    long user_id = update.getMessage().getFrom().getId();
		String text = update.getMessage().getText();

        log.info(" -> Message from {} {} (id = {}) \n Text - {}",
                user_first_name,
                user_last_name,
                user_id,
		        text);
    }

	private static void inputLogButton(Update update) {
        String user_first_name = update.getCallbackQuery().getMessage().getChat().getFirstName();
        String user_last_name = update.getCallbackQuery().getMessage().getChat().getLastName();
        long user_id = update.getCallbackQuery().getMessage().getChat().getId();
        String data = update.getCallbackQuery().getData();

		log.info(" -> Callback from {} {} (id = {}) \n Callback Data - {}",
                user_first_name,
                user_last_name,
                user_id,
                data);
    }

	private static void inputLogContact(Update update) {
		String user_first_name = update.getMessage().getFrom().getFirstName();
		String user_last_name = update.getMessage().getFrom().getLastName();
		long user_id = update.getMessage().getFrom().getId();
		Contact contact = update.getMessage().getContact();
		String contactName = contact.getFirstName();
		if (contact.getLastName() != null) {
			contactName = contactName.concat(" ").concat(contact.getLastName());
		}

		log.info(" -> Contact from {} {} (id = {}) \n Contact -> {}",
				user_first_name,
				user_last_name,
				user_id,
				contactName);
	}


}
