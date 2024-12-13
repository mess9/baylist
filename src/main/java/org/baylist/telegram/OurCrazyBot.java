//package org.baylist.telegram;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
//import org.telegram.telegrambots.longpolling.BotSession;
//import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
//import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
//import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
//import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
//    private final String TOKEN_TG = System.getenv("TOKEN_TG");
//    private final TelegramClient telegramClient;
//    private final TelegramChat telegramChat;
//    private final Button button;
//
//    public OurCrazyBot(TelegramChat telegramChat, Button button) {
//        telegramClient = new OkHttpTelegramClient(getBotToken());
//        this.telegramChat = telegramChat;
//        this.button = button;
//    }
//
//    public static void sendMessageToTelegram(SendMessage message, TelegramClient telegramClient) {
//        try {
//            telegramClient.execute(message); // Отправляем сообщение
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    @Override
//    public String getBotToken() {
//        return TOKEN_TG;
//    }
//
//    @Override
//    public LongPollingUpdateConsumer getUpdatesConsumer() {
//        return this;
//    }
//
//    @Override
//    public void consume(Update update) {
//        SendMessage message = null;
//        // todo
//        //  4. сделать команду/кнопку которой можно дополнять словарь
//        //  13. при добавлении новых категорий, производить перемещение внекатегорийных задач в добавленную категорию
//        //  7. нотификации о том что в тудуист кто-то что-то добавил (нужна бд)
//        //  8. инлайн способ взаимодействия
//        //  9. возможность работать не только с филом, а разделение на пользователей (нужна бд)
//        //  10. кнопки и состояния чата для большей интерактивности
//        //  11. нотификации о том что было бы неплохо таки сходить
//        //  12. прикрутить ai, шоб совсем модно было
//
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            message = emptySendMessage(update);
//            message = telegramChat.chat(update, message);
//        }
//        if (update.hasCallbackQuery()) {
//            // тут будет про кнопки
//        }
////        button.buttons(update, telegramClient);
//
//        sendMessageToTelegram(message);
//    }
//
//    private SendMessage emptySendMessage(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            return SendMessage.builder().text("").chatId(update.getMessage().getChatId()).build();
//        } else {
//            return SendMessage.builder().text("").chatId(update.getCallbackQuery().getMessage().getChatId()).build();
//        }
//    }
//
//    @AfterBotRegistration
//    public void afterRegistration(BotSession botSession) { //todo - разобраться бы что это такое и зачем.
//        System.out.println("Registered bot running state is: " + botSession.isRunning());
//    }
//
//    //private
//    private void sendMessageToTelegram(SendMessage message) {
//        try {
//            telegramClient.execute(outputLog(message));
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//
//}
