package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Commands {

    START("/start"),
    CLEAR("/clear"),
    VIEW("/view"),
    SYNC("/sync"),
    REPORT("/report"), //todo добавить обратную связь, что бы бот пересылал мне сообщение
    ;

    private final String command;
}
