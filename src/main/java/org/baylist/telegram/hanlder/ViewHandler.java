package org.baylist.telegram.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.service.CommonResponseService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewHandler implements DialogHandler {

	private CommonResponseService commonResponseService;

	// state VIEW
	@Override
	public void handle(ChatValue chatValue) {
		commonResponseService.view(chatValue, false);
	}


}
