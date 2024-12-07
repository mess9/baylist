package org.baylist.telegram;

import lombok.Data;

@Data
public class ChatState {

    //todo тут предполагается хранить нить разговора и состояние беседы.
    //  пример использования см тут - https://github.com/BadHard101/RemindMe7Bot/blob/master/src/main/java/com/example/remindme7bot/service/TelegramBot.java
    // данную дто нужно будет переделать по ситуации, это просто пример
    private String Title;
    private boolean expectingTitle;

    private Long taskId;
    private boolean editingTask;

    private boolean editingTitle;
    private boolean editingDescription;
    private boolean editingDeadline;

    private boolean editingNotify;
}
