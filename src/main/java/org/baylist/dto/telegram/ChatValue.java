package org.baylist.dto.telegram;

import lombok.Data;
import org.baylist.db.entity.User;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

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
		if (update.hasMessage()) {
            this.chatId = update.getMessage().getChatId();
        } else {
            this.chatId = update.getCallbackQuery().getMessage().getChatId();
	        this.isCallback = true;
        }
        this.message = SendMessage.builder().text("").chatId(chatId).build();
    }


	//region GETTER
	public String getInputText() {
		if (update.getMessage().hasText()) {
			return update.getMessage().getText();
		}
		return "";
	}

	public String getCallbackData() {
		return update.getCallbackQuery().getData();
	}

	public State getState() {
		return user.getDialog().getState();
	}

	public String getToken() {
		return "Bearer " + user.getTodoistToken();
	}

	public Long getUserId() {
		return user.getUserId();
	}
	//endregion GETTER


	//region SETTER
    public void setReplyText(String text) {
        this.message.setText(text);
    }

	public void setEditText(String text) {
		int messageId;
		if (update.hasMessage()) {
			messageId = update.getMessage().getMessageId();
		} else {
			messageId = update.getCallbackQuery().getMessage().getMessageId();
		}
		this.editMessage = EditMessageText.builder()
				.text(text)
				.chatId(chatId)
				.messageId(messageId)
				.build();
	}

    public void setReplyParseModeHtml() {
        this.message.setParseMode("html");
    }

	public void setEditReplyParseModeHtml() {
		this.editMessage.setParseMode("html");
	}

	public void setReplyKeyboard(ReplyKeyboard markup) {
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
