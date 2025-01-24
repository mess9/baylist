package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictRemoveVariantHandler implements DialogHandler {

	DictionaryService dictionaryService;
	CommonResponseService responseService;
	DictViewHandler dictViewHandler;
	MenuService menuService;
	Map<Long, Category> selectedCategory = new ConcurrentHashMap<>();

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
				Category category = dictViewHandler.getCategoryFromCallback(chatValue.getUserId(), callbackData);
				selectedCategory.put(chatValue.getUserId(), category);
				dictViewHandler.handleCategoryChoice(chatValue, category, true);
			}
		} else {
			String variants = chatValue.getInputText();
			if (dictionaryService.validateVariants(variants)) {
				List<String> variantList = Arrays.stream(variants.split("\n")).map(String::trim).toList();
				Category category = selectedCategory.get(chatValue.getUserId());
				List<String> deletedVariants = dictionaryService.removeVariants(variantList, category);
				if (!deletedVariants.isEmpty()) {
					selectedCategory.remove(chatValue.getUserId());
					menuService.dictionaryMainMenu(chatValue, false);
					chatValue.setState(State.DICT_SETTING);
					responseService.textChoiceRemoveVariant(chatValue, true);
				} else {
					responseService.textChoiceRemoveVariant(chatValue, false);
				}
			} else {
				chatValue.setReplyText("введённые варианты не прошли валидацию на корректность\n" +
						"пустой список, или меньше двух символов");
			}
		}
	}

}



