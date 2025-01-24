package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictRenameCategoryHandler implements DialogHandler {

	DictionaryService dictionaryService;
	MenuService menuService;
	Map<Long, Category> selectedCategory = new ConcurrentHashMap<>();


	// state DICT_RENAME_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				Long categoryId = Long.parseLong(callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length()));
				Long userId = chatValue.getUserId();
				var categoryDb = dictionaryService.getCategoryByCategoryIdAndUserId(categoryId, userId);
				selectedCategory.put(userId, categoryDb);
				chatValue.setEditText("прошу ввести новое название для категории - [ <b>" + categoryDb.getName() + "</b> ]");
				chatValue.setEditReplyParseModeHtml();
				chatValue.setState(State.DICT_RENAME_CATEGORY);
			}
		} else {
			Long userId = chatValue.getUserId();
			var category = selectedCategory.get(userId);
			if (category != null) {
				String newCategoryName = chatValue.getInputText();
				if (validate(newCategoryName)) {
					dictionaryService.renameCategory(category, newCategoryName);
					chatValue.setState(State.DICT_SETTING);
					selectedCategory.remove(userId);
					menuService.dictionaryMainMenu(chatValue, false);
					chatValue.setReplyText("категория переименована");
				} else {
					chatValue.setEditText("название категории не может быть пустым");
					chatValue.setEditReplyParseModeHtml();
				}
			}
		}
	}

	private boolean validate(String newCategoryName) {
		return newCategoryName != null && !newCategoryName.isBlank();
	}

}
