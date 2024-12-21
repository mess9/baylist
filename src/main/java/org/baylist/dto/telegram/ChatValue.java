package org.baylist.dto.telegram;

import lombok.Data;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
public class ChatValue {

    private Long chatId;
	private boolean isCallback;

    private Update update;
    private SendMessage message;
    private ForwardMessage forwardMessage;

	private User user;
	private UserService userService;


	public ChatValue(Update update, UserService userService) {
        this.update = update;
        if (update.hasMessage() && update.getMessage().hasText()) {
            this.chatId = update.getMessage().getChatId();
        } else {
            this.chatId = update.getCallbackQuery().getMessage().getChatId();
	        this.isCallback = true;
        }
        this.message = SendMessage.builder().text("").chatId(chatId).build();
		this.userService = userService;
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

    public void setReplyParseModeHtml() {
        this.message.setParseMode("html");
    }

    public void setReplyKeyboard(InlineKeyboardMarkup markup) {
        this.message.setReplyMarkup(markup);
    }

	public void setState(State state) {
		user.getDialog().setState(state);
		userService.saveUser(user);
	}
	//endregion SETTER


}
