package org.baylist.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.todoist.service.TodoistService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.baylist.util.log.TgLog.inputLog;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramChat {

    private TodoistService todoist;


    public void chat(Update update, TelegramClient telegramClient) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        inputLog(update);

//        if (todoist.storageIsEmpty()) {
//            todoist.syncData();
//        }
//        SendMessage message;
//
//        if (message_text.equalsIgnoreCase("Проекты")) {
//            message = SendMessage.builder()
//                    .chatId(chat_id)
//                    .text(todoist.getProjects())
//                    .build();
//
//        } else if (todoist.getProjectsName().contains(message_text.toLowerCase())) {
//            message = SendMessage.builder()
//                    .chatId(chat_id)
//                    .text("подробности по проекту: \n" + todoist.getProjectByName(message_text.toLowerCase()))
//                    .build();
//        } else {
//            message = SendMessage.builder()
//                    .chatId(chat_id)
//                    .text("я отвечаю всегда однообразно")
//                    .build();
//        }
//
//        try {
//            telegramClient.execute(outputLog(message));
//        } catch (TelegramApiException e) {
//            TelegramChat.log.error(e.getMessage());
//        }
    }

}
