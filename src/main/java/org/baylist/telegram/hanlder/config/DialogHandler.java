package org.baylist.telegram.hanlder.config;

import org.baylist.dto.telegram.ChatValue;

@FunctionalInterface
public interface DialogHandler {

	void handle(ChatValue chatValue);

}
