package org.baylist.telegram;

import lombok.extern.slf4j.Slf4j;
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


@Component
@Slf4j
public class OurCrazyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String TOKEN_TG = System.getenv("TOKEN_TG");
    private final TelegramClient telegramClient;
    private final TelegramChat telegramChat;
    private final Button button;

    public OurCrazyBot(TelegramChat telegramChat, Button button) {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        this.telegramChat = telegramChat;
        this.button = button;
    }

    @Override
    public String getBotToken() {
        return TOKEN_TG;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    public static void sendMessage(SendMessage message, TelegramClient telegramClient) {
        try {
            telegramClient.execute(message); // Отправляем сообщение
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void consume(Update update) {
        // todo
        //  1. получить текущее состояние из тудуиста (желательно делать один раз в начале разговора, или если там пусто) - done
        //  2. сделать кнопку - текущие задачи - при нажатии на которую будет выведена структура проекта buylist
        //  3. кнопочка - добавить задачи - и написать добавление задач в проект buylist
        //  3.5 кнопочка - добавить задачи - и написать добавление задач в проект buylist, с разбитием на категории(сравнивая по словарю)
        //  не нашедшееся в словаре записывать вне категории
        //  4. сделать команду которой можно дополнять словарь
        //  5. ...
        //  6. profit!

        if (update.hasMessage() && update.getMessage().hasText()) {
            telegramChat.chat(update, telegramClient);
            button.buttons(update, telegramClient); //возможно кнопки следует всунуть в чат, а не сюда. пока хз, пока они нужны только в рамках п.2

        }


    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) { //todo - разобраться бы что это такое и зачем.
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }


}
