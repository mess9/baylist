package org.baylist.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.service.TodoistService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.baylist.util.log.TgLog.inputLog;
import static org.baylist.util.log.TgLog.outputLog;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramChat {

    private TodoistService todoist;

    private static void sendMessage(TelegramClient telegramClient, SendMessage message) {
        try {
            telegramClient.execute(outputLog(message));
        } catch (TelegramApiException e) {
            TelegramChat.log.error(e.getMessage());
        }
    }

    public void chat(Update update, TelegramClient telegramClient) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        inputLog(update);

        if (todoist.storageIsEmpty()) {
            todoist.syncData();
        }
        SendMessage message;
        String text;

        if (message_text.equals("/clear")) {
            text = todoist.clearBuyList();
        } else {
            text = todoist.sendTaskToTodoist(message_text);
        }


        message = SendMessage.builder()
                .chatId(chat_id)
                .text(text)
                .build();
        sendMessage(telegramClient, message);
    }

}
