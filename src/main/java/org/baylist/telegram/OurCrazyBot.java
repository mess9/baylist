package org.baylist.telegram;

import lombok.extern.slf4j.Slf4j;
import org.baylist.dto.telegram.ChatState;
import org.baylist.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.baylist.util.log.TgLog.outputLog;


@Component
@Slf4j
public class OurCrazyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

	private final String TOKEN_TG = System.getenv("TOKEN_TG");
	private final UserService userService;
	private final TelegramClient telegramClient;
	private final TelegramChat telegramChat;
	private final Button button;

	public OurCrazyBot(TelegramChat telegramChat, Button button, UserService userService) {
		telegramClient = new OkHttpTelegramClient(getBotToken());
		this.telegramChat = telegramChat;
		this.button = button;
		this.userService = userService;
	}

	@Override
	public String getBotToken() {
		return TOKEN_TG;
		// попробовать подключиться к тестовму контуру
		// - https://habr.com/ru/companies/selectel/articles/763286/
	}

	@Override
	public void consume(Update update) {
		ChatState chatState = new ChatState(update);
		userService.checkUser(chatState);
		// todo
		//  4. сделать команду/кнопку которой можно дополнять словарь
		//  13. при добавлении новых категорий, производить перемещение внекатегорийных задач в добавленную категорию
		//  7. нотификации о том что в тудуист кто-то что-то добавил (нужна бд)
		//  8. инлайн способ взаимодействия
		//  9. возможность работать не только с филом, а разделение на пользователей (нужна бд)
		//  10. кнопки и состояния чата для большей интерактивности
		//  11. нотификации о том что было бы неплохо таки сходить
		//  12. прикрутить ai, шоб совсем модно было


		if (chatState.getUpdate().hasMessage() && chatState.getUpdate().getMessage().hasText()) {
			telegramChat.chat(chatState);
		} else if (chatState.getUpdate().hasCallbackQuery()) {
			button.buttons(chatState);
		}

		sendMessageToTelegram(chatState);
	}

	@Override
	public LongPollingUpdateConsumer getUpdatesConsumer() {
		return this;
	}

	@AfterBotRegistration
	public void afterRegistration(BotSession botSession) { //todo - разобраться бы что это такое и зачем.
		System.out.println("Registered bot running state is: " + botSession.isRunning());
	}

	//private
	private void sendMessageToTelegram(ChatState chatState) {
		try {
			telegramClient.execute(outputLog(chatState.getMessage()));
			if (chatState.getForwardMessage() != null) {
				telegramClient.execute(chatState.getForwardMessage());
			}
		} catch (TelegramApiException e) {
			log.error(e.getMessage());
		}
	}

}
