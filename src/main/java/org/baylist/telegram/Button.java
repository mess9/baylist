package org.baylist.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.baylist.telegram.OurCrazyBot.sendMessage;

@Component
public class Button {

    //todo тут пока наброски о том как обрабатывать кнопки
    public void buttons(Update update, TelegramClient telegramClient) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

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

            sendMessage(message, telegramClient);
        }

        if (update.hasCallbackQuery()) {

            String id = update.getCallbackQuery().getId();
            String data = update.getCallbackQuery().getData();
            String chatInstance = update.getCallbackQuery().getChatInstance();
            String gameShortName = update.getCallbackQuery().getGameShortName();

            System.out.println("id: " + id + " data: " + data + " chatInstance: " + chatInstance + " gameShortName: " + gameShortName);

            SendMessage message = SendMessage.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId())
                    .text("**хуй**")
                    .build();

            sendMessage(message, telegramClient);
        }
    }

}
