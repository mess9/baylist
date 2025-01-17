package org.baylist.telegram;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.MenuService;
import org.baylist.service.UserService;
import org.baylist.telegram.hanlder.config.CommandChecker;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

import static org.baylist.util.log.TgLog.inputLog;
import static org.baylist.util.log.TgLog.outputLog;


@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

	String TOKEN_TG = System.getenv("TOKEN_TG");
	UserService userService;
	MenuService menuService;
	TelegramClient telegramClient;
	CommandChecker commandChecker;
	Map<State, DialogHandler> stateHandlers;


	public Bot(UserService userService,
	           CommandChecker commandChecker,
	           MenuService menuService,
	           Map<State, DialogHandler> stateHandlers) {
		telegramClient = new OkHttpTelegramClient(getBotToken());
		this.userService = userService;
		this.commandChecker = commandChecker;
		this.stateHandlers = stateHandlers;
		this.menuService = menuService;
	}


	@Override
	public void consume(Update update) {
		inputLog(update);
		ChatValue chatValue = new ChatValue(update);
		userService.checkUser(chatValue);
		commandChecker.checkCommandInput(chatValue);
		State state = chatValue.getState();

		stateHandlers.get(state).handle(chatValue);

		userService.saveUserInCache(chatValue.getUser());

		menuService.defaultMenu(chatValue);
		sendMessageToTelegram(chatValue);
	}


	@Override
	public LongPollingUpdateConsumer getUpdatesConsumer() {
		return this;
	}

	@Override
	public String getBotToken() {
		return TOKEN_TG;
	}

	@AfterBotRegistration
	@SuppressWarnings("unused")
	public void afterRegistration(BotSession botSession) {
		log.info("Registered bot running state is: {}", botSession.isRunning());
		commandChecker.setCommandList(telegramClient);
	}

	//private
	private void sendMessageToTelegram(ChatValue chatValue) {
		try {
			if (chatValue.getEditMessage() != null) {
				telegramClient.execute(outputLog(chatValue.getEditMessage()));
			} else {
				telegramClient.execute(outputLog(chatValue.getMessage()));
				if (chatValue.getForwardMessage() != null) {
					telegramClient.execute(chatValue.getForwardMessage());
				}
			}
		} catch (TelegramApiException e) {
			log.error(e.getMessage());
		}
	}

}
