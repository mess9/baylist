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
    ;

    private final String command;
}
