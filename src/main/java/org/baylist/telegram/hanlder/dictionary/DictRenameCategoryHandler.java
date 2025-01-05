package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.db.entity.Category;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class DictRenameCategoryHandler implements DialogHandler {

	private final DictionaryService dictionaryService;
	private final Map<Long, Category> selectedCategory = new ConcurrentHashMap<>();


	// state DICT_RENAME_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				String category = callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length());
				Long userId = chatValue.getUser().getUserId();
				var categoryDb = dictionaryService.getCategoryByName(category);
				selectedCategory.put(userId, categoryDb);
				chatValue.setEditText("прошу ввести новое название для категории - [ <b>" + category + "</b> ]");
				chatValue.setEditReplyParseModeHtml();
				chatValue.setState(State.DICT_RENAME_CATEGORY);
			}
		} else {
			Long userId = chatValue.getUser().getUserId();
			var category = selectedCategory.get(userId);
			if (category != null) {
				String newCategoryName = chatValue.getInputText();
				if (validate(newCategoryName)) {
					dictionaryService.renameCategory(category, newCategoryName);
					chatValue.setState(State.DICT_SETTING);
					selectedCategory.remove(userId);
					dictionaryService.settingsMainMenu(chatValue, false);
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
