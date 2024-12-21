//package org.baylist.telegram;
//
//import lombok.extern.slf4j.Slf4j;
//import org.baylist.dto.telegram.ChatValue;
//import org.baylist.service.UserService;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
//import org.telegram.telegrambots.longpolling.BotSession;
//import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
//import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
//import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
//import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.meta.generics.TelegramClient;
//
//import static org.baylist.util.log.TgLog.outputLog;
//
//
//@Component
//@Slf4j
//public class OurCrazyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
//
//	private final String TOKEN_TG = System.getenv("TOKEN_TG");
//	private final UserService userService;
//	private final TelegramClient telegramClient;
//	private final TelegramChat telegramChat;
//	private final Button button;
//
//	public OurCrazyBot(TelegramChat telegramChat, Button button, UserService userService) {
//		telegramClient = new OkHttpTelegramClient(getBotToken());
//		this.telegramChat = telegramChat;
//		this.button = button;
//		this.userService = userService;
//	}
//
//	@Override
//	public String getBotToken() {
//		return TOKEN_TG;
//		// попробовать подключиться к тестовму контуру
//		// - https://habr.com/ru/companies/selectel/articles/763286/
//	}
//
//	@Override
//	public void consume(Update update) {
//		ChatValue chatValue = new ChatValue(update);
//		userService.checkUser(chatValue);
//
//		if (chatValue.getUpdate().hasMessage() && chatValue.getUpdate().getMessage().hasText()) {
//			telegramChat.chat(chatValue);
//		} else if (chatValue.getUpdate().hasCallbackQuery()) {
//			button.buttons(chatValue);
//		}
//
//		sendMessageToTelegram(chatValue);
//	}
//
//	@Override
//	public LongPollingUpdateConsumer getUpdatesConsumer() {
//		return this;
//	}
//
//	@AfterBotRegistration
//	public void afterRegistration(BotSession botSession) { //todo - разобраться бы что это такое и зачем.
//		System.out.println("Registered bot running state is: " + botSession.isRunning());
//	}
//
//	//private
//	private void sendMessageToTelegram(ChatValue chatValue) {
//		try {
//			telegramClient.execute(outputLog(chatValue.getMessage()));
//			if (chatValue.getForwardMessage() != null) {
//				telegramClient.execute(chatValue.getForwardMessage());
//			}
//		} catch (TelegramApiException e) {
//			log.error(e.getMessage());
//		}
//	}
//
//}
