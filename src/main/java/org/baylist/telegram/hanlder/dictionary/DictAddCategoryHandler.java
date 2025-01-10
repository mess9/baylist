package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.CommonResponseService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DictAddCategoryHandler implements DialogHandler {

	private DictionaryService dictionaryService;
	private CommonResponseService commonResponseService;

	//todo валидация на уникальность категорий пользователя


	// state DICT_ADD_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				chatValue.setReplyText("ок, продолжим редактировать словарик");
				dictionaryService.dictionaryMainMenu(chatValue, true);
			} else if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				commonResponseService.cancelMessage(chatValue);
			}
		} else {
			String category = chatValue.getUpdate().getMessage().getText().trim().toLowerCase();
			Long userId = chatValue.getUser().getUserId();
			if (dictionaryService.addDictCategory(category, userId)) {
				dictionaryService.settingsShortMenu(chatValue,
						"категория - [ <b>" + category + "</b> ] - добавлена",
						false);
			} else {
				dictionaryService.settingsShortMenu(chatValue,
						"такая категория уже существует",
						false);
			}
			chatValue.setState(State.DICT_SETTING);
			chatValue.setReplyParseModeHtml();
		}
	}


}
