package org.baylist.telegram.handler.config;

import org.baylist.dto.telegram.ChatValue;

@FunctionalInterface
public interface DialogHandler {

	void handle(ChatValue chatValue);

}
