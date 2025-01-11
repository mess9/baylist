package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.SelectedCategoryState;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.service.TgButtonService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictRemoveCategoryHandler implements DialogHandler {

	Map<Long, SelectedCategoryState> selectedCategoryState = new ConcurrentHashMap<>();
	DictionaryService dictionaryService;
	CommonResponseService commonResponseService;
	TgButtonService tgButtonService;
	MenuService menuService;

	// state DICT_REMOVE_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				String category = callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length());
				List<String> categories = dictionaryService.getCategories();
				Long userId = chatValue.getUser().getUserId();
				if (selectedCategoryState.containsKey(userId)) {
					if (!selectedCategoryState.get(userId).getSelectedCategories().contains(category)) {
						selectedCategoryState.get(userId).getSelectedCategories().add(category);
					} else {
						selectedCategoryState.get(userId).getSelectedCategories().remove(category);
					}
				} else {
					selectedCategoryState.put(userId, new SelectedCategoryState(categories, new ArrayList<>(List.of(category))));
				}
				commonResponseService.textChoiceRemoveCategory(chatValue, true);
				tgButtonService.categoriesChoiceKeyboardEdit(chatValue, State.DICT_REMOVE_CATEGORY,
						selectedCategoryState.get(userId));
			} else if (callbackData.startsWith(Callbacks.REMOVE_CATEGORY.getCallbackData())) {
				List<String> selectedCategories = selectedCategoryState.get(chatValue.getUser().getUserId()).getSelectedCategories();
				selectedCategories.forEach(dictionaryService::removeCategory);
				if (selectedCategories.size() > 1) {
					StringBuilder sb = new StringBuilder();
					selectedCategories.forEach(c -> sb.append(" - <b>").append(c).append("</b>\n"));
					dictionaryService.settingsShortMenu(chatValue,
							"категории:\n" + sb + "\nудалены",
							true);
				} else {
					dictionaryService.settingsShortMenu(chatValue,
							"категория - [ <b>" + selectedCategories.getFirst() + "</b> ] - удалена",
							true);
				}
				chatValue.setState(State.DICT_SETTING);
				chatValue.setReplyParseModeHtml();
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
				selectedCategoryState.remove(chatValue.getUser().getUserId());
			}
		}
	}
}



