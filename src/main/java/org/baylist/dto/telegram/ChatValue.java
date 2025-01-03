package org.baylist.dto.telegram;

import lombok.Data;
import org.baylist.db.entity.User;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
public class ChatValue {

    private Long chatId;
	private boolean isCallback;

    private Update update;
    private SendMessage message;
    private ForwardMessage forwardMessage;
	private EditMessageText editMessage;

	private User user;


	public ChatValue(Update update) {
        this.update = update;
        if (update.hasMessage() && update.getMessage().hasText()) {
            this.chatId = update.getMessage().getChatId();
        } else {
            this.chatId = update.getCallbackQuery().getMessage().getChatId();
	        this.isCallback = true;
        }
        this.message = SendMessage.builder().text("").chatId(chatId).build();
    }


	//region GETTER
	public String getInputText() {
		return update.getMessage().getText();
	}

	public String getCallbackData() {
		return update.getCallbackQuery().getData();
	}

	public State getState() {
		return user.getDialog().getState();
	}
	//endregion GETTER


	//region SETTER
    public void setReplyText(String text) {
        this.message.setText(text);
    }

	public void setEditMessage(String text) {
		this.editMessage = EditMessageText.builder()
				.text(text)
				.chatId(chatId)
				.messageId(update.getCallbackQuery().getMessage().getMessageId())
				.build();
	}

    public void setReplyParseModeHtml() {
        this.message.setParseMode("html");
    }

	public void setEditReplyParseModeHtml() {
		this.editMessage.setParseMode("html");
	}

    public void setReplyKeyboard(InlineKeyboardMarkup markup) {
        this.message.setReplyMarkup(markup);
    }

	public void setEditReplyKeyboard(InlineKeyboardMarkup markup) {
		this.editMessage.setReplyMarkup(markup);
	}

	public void setState(State state) {
		user.getDialog().setState(state);
	}
	//endregion SETTER


}
