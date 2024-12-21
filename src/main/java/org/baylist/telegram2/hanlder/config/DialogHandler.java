package org.baylist.telegram2.hanlder.config;

import org.baylist.dto.telegram.ChatValue;

@FunctionalInterface
public interface DialogHandler {

	void handle(ChatValue chatValue);

}
