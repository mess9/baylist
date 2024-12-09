package org.baylist.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.baylist.telegram.OurCrazyBot.sendMessageToTelegram;

@Component
public class Button {

    //ограничения полученные опытным путём
    // максимальное количество кнопок в одной строке - 8
    // - в название влезает 4 символа
    // - 3 если с телефона
    // максимальное в высоту - хз, пробовал 20, 20 влезает.

    public void buttons(Update update, TelegramClient telegramClient) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

//            InlineKeyboardButton button1 = InlineKeyboardButton.builder()
//                    .text("кнопка раз")
//                    .callbackData("data")
//                    .build();
//            InlineKeyboardButton button2 = new InlineKeyboardButton("Кнопка 2");
//            button2.setCallbackData("command2");

            InlineKeyboardButton button1 = new InlineKeyboardButton("о");
            button1.setCallbackData("command2");
            InlineKeyboardButton button2 = new InlineKeyboardButton("о");
            button2.setCallbackData("command2");
            InlineKeyboardButton button3 = new InlineKeyboardButton("о");
            button3.setCallbackData("command2");
            InlineKeyboardButton button4 = new InlineKeyboardButton("о");
            button4.setCallbackData("command2");
            InlineKeyboardButton button5 = new InlineKeyboardButton("л");
            button5.setCallbackData("command2");
            InlineKeyboardButton button6 = new InlineKeyboardButton("лл");
            button6.setCallbackData("command2");
            InlineKeyboardButton button7 = new InlineKeyboardButton("л");
            button7.setCallbackData("command2");
            InlineKeyboardButton button8 = new InlineKeyboardButton("п");
            button8.setCallbackData("command2");
            InlineKeyboardButton button9 = new InlineKeyboardButton("9");
            button9.setCallbackData("command2");
            InlineKeyboardButton button10 = new InlineKeyboardButton("0");
            button10.setCallbackData("command2");
            InlineKeyboardButton button11 = new InlineKeyboardButton("1");
            button11.setCallbackData("command2");
            InlineKeyboardButton button12 = new InlineKeyboardButton("2");
            button12.setCallbackData("command2");
            InlineKeyboardButton button13 = new InlineKeyboardButton("3");
            button13.setCallbackData("command2");
            InlineKeyboardButton button14 = new InlineKeyboardButton("4");
            button14.setCallbackData("command2");
            InlineKeyboardButton button15 = new InlineKeyboardButton("5");
            button15.setCallbackData("command2");
            InlineKeyboardButton button16 = new InlineKeyboardButton("6");
            button16.setCallbackData("command2");


//            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16)));
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button2),
                    new InlineKeyboardRow(button3),
                    new InlineKeyboardRow(button4),
                    new InlineKeyboardRow(button5),
                    new InlineKeyboardRow(button6),
                    new InlineKeyboardRow(button7),
                    new InlineKeyboardRow(button8),
                    new InlineKeyboardRow(button9),
                    new InlineKeyboardRow(button10),
                    new InlineKeyboardRow(button11),
                    new InlineKeyboardRow(button12),
                    new InlineKeyboardRow(button13),
                    new InlineKeyboardRow(button14),
                    new InlineKeyboardRow(button15),
                    new InlineKeyboardRow(button16),
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button1)
            ));
//            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button1, button2)));

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберите одну из кнопок: ")
                    .replyMarkup(markup)
                    .build();

            sendMessageToTelegram(message, telegramClient);
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

            sendMessageToTelegram(message, telegramClient);
        }
    }

}
