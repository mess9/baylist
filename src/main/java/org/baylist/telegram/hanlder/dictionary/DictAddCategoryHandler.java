package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictAddCategoryHandler implements DialogHandler {

	DictionaryService dictionaryService;
	CommonResponseService responseService;
	MenuService menuService;

	//todo валидация на уникальность категорий пользователя


	// state DICT_ADD_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				chatValue.setReplyText("ок, продолжим редактировать словарик");
				menuService.dictionaryMainMenu(chatValue, true);
			} else if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			}
		} else {
			String category = chatValue.getUpdate().getMessage().getText().trim().toLowerCase();
			Long userId = chatValue.getUserId();
			if (dictionaryService.addDictCategory(category, userId)) {
				dictionaryService.settingsShortMenu(chatValue,
						"категория - [ <b>" + category + "</b> ] - добавлена",
						false);
			} else {
				dictionaryService.settingsShortMenu(chatValue,
						"""
								такая категория уже существует
								 или
								 введённое имя боту не понравилось
								 (пусто имя, имя меньше 2-х букв)""",
						false);
			}
			chatValue.setState(State.DICT_SETTING);
			chatValue.setReplyParseModeHtml();
		}
	}


}
