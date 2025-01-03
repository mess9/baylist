package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.ResponseService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DictRemoveVariantHandler implements DialogHandler {

	private DictionaryService dictionaryService;
	private ResponseService responseService;
	private DictViewHandler dictViewHandler;

	// state DICT_REMOVE_VARIANT
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				dictionaryService.settingsMainMenu(chatValue);
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				dictViewHandler.handleCategoryChoice(chatValue, callbackData);
			}
		} else {
			String variants = chatValue.getInputText();
			if (validate(variants)) {
				dictionaryService.removeVariants(variants);
				dictionaryService.settingsMainMenu(chatValue);
				chatValue.setState(State.DICT_SETTING);
				responseService.textChoiceRemoveVariant(chatValue, true);
			} else {
				responseService.textChoiceRemoveVariant(chatValue, false);
				//todo в это ветвление не попасть, нужна валидация на список вариантов
			}

		}
	}

	private boolean validate(String variants) {
		return variants != null && !variants.isBlank();
	}
}



