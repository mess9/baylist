package org.baylist.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.todoist.service.TodoistService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
@AllArgsConstructor
public class TelegramChat {

    private TodoistService todoist;


    public void chat(Update update, TelegramClient telegramClient) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        log.info("\nin chat - {}\n get message - {}", chat_id, message_text);

        if (todoist.storageIsEmpty()) {
            todoist.syncData();
        }

        SendMessage message;
        String projectsName = todoist.getProjectsName();
        System.out.println(projectsName);

        if (message_text.equalsIgnoreCase("Проекты")) {
            message = SendMessage.builder()
                    .chatId(chat_id)
                    .text(todoist.getProjects())
                    .build();

        } else if (todoist.getProjectsName().contains(message_text.toLowerCase())) {
            message = SendMessage.builder()
                    .chatId(chat_id)
                    .text("подробности по проекту: \n" + todoist.getProjectByName(message_text.toLowerCase()))
                    .build();
        } else {
            message = SendMessage.builder()
                    .chatId(chat_id)
                    .text("я отвечаю всегда однообразно")
                    .build();
        }

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


}
