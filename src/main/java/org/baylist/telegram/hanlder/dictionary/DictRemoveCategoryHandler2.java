package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.SelectedCategoryState;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.ResponseService;
import org.baylist.service.TgButtonService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class DictRemoveCategoryHandler2 implements DialogHandler {

	private final Map<Long, SelectedCategoryState> selectedCategoryState = new ConcurrentHashMap<>();
	private DictionaryService dictionaryService;
	private ResponseService responseService;
	private TgButtonService tgButtonService;

	// state DICT_REMOVE_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				chatValue.setReplyText("ок, продолжим редактировать словарик");
				dictionaryService.settingsMainMenu(chatValue);
			} else if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
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
				responseService.textChoiceRemoveCategory(chatValue, true);
				tgButtonService.categoriesChoiceKeyboardEdit(chatValue, State.DICT_REMOVE_CATEGORY,
						selectedCategoryState.get(userId));
			} else if (callbackData.startsWith(Callbacks.REMOVE_CATEGORY.getCallbackData())) {
				List<String> selectedCategories = selectedCategoryState.get(chatValue.getUser().getUserId()).getSelectedCategories();
				selectedCategories.forEach(category -> dictionaryService.removeCategory(category));
				if (selectedCategories.size() > 1) {
					StringBuilder sb = new StringBuilder();
					selectedCategories.forEach(c -> sb.append(" - <b>").append(c).append("</b>\n"));
					dictionaryService.settingsShortMenu(chatValue, "категории:\n" + sb + "\nудалены");
				} else {
					dictionaryService.settingsShortMenu(chatValue,
							"категория - [ <b>" + selectedCategories.getFirst() + "</b> ] - удалена");
				}
				chatValue.setState(State.DICT_SETTING);
				chatValue.setReplyParseModeHtml();
			}
		}
	}
}



