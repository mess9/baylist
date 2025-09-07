package org.baylist.dto.telegram;

import lombok.Data;
import org.baylist.db.entity.User;
import org.baylist.service.DialogService;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import static org.baylist.util.SpringBeans.getBean;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Data
public class ChatValue {

    private Long chatId;
	private boolean isCallback;

    private Update update;
    private SendMessage message;
    private ForwardMessage forwardMessage;
	private EditMessageText editMessage;

	private User user;
	private State state;


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

	public String getToken() {
		return "Bearer " + user.todoistToken();
	}

	public Long getUserId() {
		return user.userId();
	}

	public State getState() {
		if (state == null && user != null) {
			state = getBean(DialogService.class).getState(user.userId());
		}
		return state;
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
	    this.message.setParseMode(HTML);
    }

	@SuppressWarnings("unused")
	public void setReplyParseModeMarkdown() {
		this.message.setParseMode(MARKDOWN);
	}

	@SuppressWarnings("unused")
	public void setReplyParseModeOff() {
		this.message.setParseMode(null);
	}

	public void setEditReplyParseModeHtml() {
		this.editMessage.setParseMode(HTML);
	}

	public void setReplyKeyboard(ReplyKeyboard markup) {
        this.message.setReplyMarkup(markup);
    }

	public void setEditReplyKeyboard(InlineKeyboardMarkup markup) {
		this.editMessage.setReplyMarkup(markup);
	}

	public void setState(State state) {
		this.state = state;
		if (this.user != null) {
			try {
				getBean(DialogService.class).setState(this.user.userId(), state);
			} catch (Exception e) {
				System.out.println("Failed to set state for user " + this.user.userId() + ": " + e.getMessage());
			}
		}
	}

	//endregion SETTER


}
