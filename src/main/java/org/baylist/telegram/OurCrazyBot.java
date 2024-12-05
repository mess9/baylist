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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;


@Component
@Slf4j
public class OurCrazyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String TOKEN_TG = System.getenv("TOKEN_TG");
    private final TelegramClient telegramClient;
    private final TelegramChat telegramChat;

    public OurCrazyBot(TelegramChat telegramChat) {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        this.telegramChat = telegramChat;
    }

    @Override
    public String getBotToken() {
        return TOKEN_TG;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            telegramChat.chat(update, telegramClient);

            //1. получить текущее состояние из тудуиста (желательно делать один раз в начале разговора, или если там пусто) -done
            //2. сделать кнопку - текущие задачи - при нажатии на которую будет выведена структура проекта buylist
            //3. кнопочка - добавить задачи - и написать добавление задач в проект buylist
            //3.5 кнопочка - добавить задачи - и написать добавление задач в проект buylist, с разбитием на категории(сравнивая по словарю)
            // не нашедшееся в словаре записывать вне категории
            //4. сделать команду которой можно дополнять словарь
            //5. ...
            //6. profit!
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

//            InlineKeyboardButton button1 = new InlineKeyboardButton("Кнопка 1");
//            button1.setCallbackData("command1");
//            button1.validate();
            InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                    .text("кнопка раз")
                    .callbackData("data")
                    .build();
            InlineKeyboardButton button2 = new InlineKeyboardButton("Кнопка 2");
            button2.setCallbackData("command2");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button1, button2)));

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберите одну из кнопок: ")
                    .replyMarkup(markup)
                    .build();

            if (message.getText().equalsIgnoreCase("Кнопка 1")) {
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text("**хуй**")
                        .replyMarkup(markup)
                        .build();
            }


            try {
                telegramClient.execute(message); // Отправляем сообщение
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else if (update.hasCallbackQuery()) {

            String id = update.getCallbackQuery().getId();
            String data = update.getCallbackQuery().getData();
            String chatInstance = update.getCallbackQuery().getChatInstance();
            String gameShortName = update.getCallbackQuery().getGameShortName();

            System.out.println("id: " + id + " data: " + data + " chatInstance: " + chatInstance + " gameShortName: " + gameShortName);

            SendMessage message = SendMessage.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId())
                    .text("**хуй**")
//                    .replyMarkup(markup)
                    .build();

            try {
                telegramClient.execute(message); // Отправляем сообщение
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }


    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) { //todo - разобраться бы что это такое и зачем.
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }


}
