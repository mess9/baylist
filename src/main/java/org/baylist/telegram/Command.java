package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Commands;
import org.baylist.service.TodoistService;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class Command {

    private TodoistService todoist;

    //todo тут будет логика по обработке команд

    public String commandHandler(String updateMessage) {
        if (Arrays.stream(Commands.values()).anyMatch(c -> c.getCommand().equals(updateMessage))) {
            switch (updateMessage) {
                case "/clear" -> {
                    return todoist.clearBuyList(); //todo спрашивать подтверждение действия кнопками
                }
                case "/view" -> {
                    return todoist.getBuylistProject();
                }
                case "/sync" -> {
                    todoist.syncBuyListData();
                    return "данные синхронизированы с todoist";
                }
            }
        }
        return "";
    }


}
