package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.HistoryService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictRemoveVariantHandler implements DialogHandler {

	DictionaryService dictionaryService;
	CommonResponseService responseService;
	DictViewHandler dictViewHandler;
	MenuService menuService;
	HistoryService historyService;

	// state DICT_REMOVE_VARIANT
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				dictViewHandler.handleCategoryChoice(chatValue, callbackData, true);
			}
		} else {
			String variants = chatValue.getInputText();
			if (dictionaryService.validate(variants)) {
				List<String> variantList = Arrays.stream(variants.split("\n")).toList();
				dictionaryService.removeVariants(variantList);
				historyService.changeDict(chatValue.getUser().getUserId(), Action.REMOVE_VARIANT, variantList.toString());
				menuService.dictionaryMainMenu(chatValue, false);
				chatValue.setState(State.DICT_SETTING);
				responseService.textChoiceRemoveVariant(chatValue, true);
			} else {
				responseService.textChoiceRemoveVariant(chatValue, false);
			}

		}
	}

}



