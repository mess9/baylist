package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Commands;
import org.baylist.service.TodoistService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class Command {

    private TodoistService todoist;

    @NotNull
    private static SendMessage notCommand(SendMessage message) {
        message.setText("");
        return message;
    }

    @NotNull
    private static SendMessage start(SendMessage message) {
        message.setText("""
                yay!, приветствую.
                этот бот написал филом, что бы отправлять ему список покупок
                
                если вы не знаете кто такой фил, вы явно обратились к этому боту по ошибке,
                но всё равно будьте вы счастливы, но только если вы хороший человек,
                если не хороший, например путин или что-то похожее, то пожалуйста убей себя каким нибудь болезненным и публичным способом.
                 ня)
                с любовью. фил.
                """);
        return message;
    }

    public SendMessage commandHandler(String updateMessage, SendMessage message) {
        if (Arrays.stream(Commands.values()).anyMatch(c -> c.getCommand().equals(updateMessage))) {
            if (updateMessage.equals(Commands.CLEAR.getCommand())) {
                return clear(message);
            } else if (updateMessage.equals(Commands.VIEW.getCommand())) {
                return view(message);
            } else if (updateMessage.equals(Commands.SYNC.getCommand())) {
                return sync(message);
            } else if (updateMessage.equals(Commands.START.getCommand())) {
                return start(message);
            }
        }
        return notCommand(message);
    }

    @NotNull
    private SendMessage sync(SendMessage message) {
        todoist.syncBuyListData();
        message.setText("данные синхронизированы с todoist");
        return message;
    }

    @NotNull
    private SendMessage clear(SendMessage message) {
        message.setText(todoist.clearBuyList());
        return message;
    }

    @NotNull
    private SendMessage view(SendMessage message) {
        message.setText(todoist.getBuylistProject());
        message.setParseMode("html");
        return message;
    }
}
