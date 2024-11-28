package org.baylist.telegram;

import lombok.extern.slf4j.Slf4j;
import org.baylist.todoist.controller.TodoistService;
import org.baylist.todoist.dto.Project;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;


@Component
@Slf4j
public class OurCrazyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String TOKEN_TG = System.getenv("TOKEN_TG");

    private final TelegramClient telegramClient;
    @Autowired
    TodoistService todoistService;

    public OurCrazyBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
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
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            log.info("get message - {}", message_text);

            List<Project> projects = todoistService.getProjects();
            System.out.println(projects);

            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("я отвечаю всегда однообразно")
                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }


    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
