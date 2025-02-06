package org.baylist.telegram.handler;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.service.CommonResponseService;
import org.baylist.telegram.handler.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewHandler implements DialogHandler {

	private CommonResponseService responseService;

	// state VIEW
	@Override
	public void handle(ChatValue chatValue) {
		responseService.checkAndView(chatValue, false);
	}

}
