package org.baylist.telegram.handler.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.SelectedCategoryState;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.service.TgButtonService;
import org.baylist.telegram.handler.config.DialogHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictRemoveCategoryHandler implements DialogHandler {

	Map<Long, SelectedCategoryState> selectedCategoryState = new ConcurrentHashMap<>();
	DictionaryService dictionaryService;
	CommonResponseService responseService;
	TgButtonService tgButtonService;
	MenuService menuService;

	// state DICT_REMOVE_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				List<Category> allCategories = dictionaryService.getCategoriesByUserId(chatValue.getUserId());

				Long removalCategoryId = Long.parseLong(callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length()));
				Category removalCategory = allCategories.stream().filter(c -> Objects.equals(c.id(), removalCategoryId)).findFirst().orElse(null);

				Long userId = chatValue.getUserId();

				if (selectedCategoryState.containsKey(userId)) {
					SelectedCategoryState selectedState = selectedCategoryState.get(userId);
					if (!selectedState.getSelectedCategories().contains(removalCategory)) {
						selectedState.getSelectedCategories().add(removalCategory);
					} else {
						selectedState.getSelectedCategories().remove(removalCategory);
					}
				} else {
					if (removalCategory != null) {
						selectedCategoryState.put(userId, new SelectedCategoryState(allCategories, new ArrayList<>(List.of(removalCategory))));
					}
				}
				responseService.textChoiceRemoveCategory(chatValue);
				tgButtonService.categoriesChoiceKeyboardEdit(chatValue, State.DICT_REMOVE_CATEGORY,
						selectedCategoryState.get(userId));
			} else if (callbackData.startsWith(Callbacks.REMOVE_CATEGORY.getCallbackData())) {
				List<Category> selectedCategories = selectedCategoryState.get(chatValue.getUserId()).getSelectedCategories();
				dictionaryService.removeCategory(selectedCategories);
				if (selectedCategories.size() > 1) {
					StringBuilder sb = new StringBuilder();
					selectedCategories.forEach(c -> sb.append(" - <b>").append(c.name()).append("</b>\n"));
					dictionaryService.settingsShortMenu(chatValue,
							"категории:\n" + sb + "\nудалены",
							true);
				} else {
					dictionaryService.settingsShortMenu(chatValue,
							"категория - [ <b>" + selectedCategories.getFirst().name() + "</b> ] - удалена",
							true);
				}
				selectedCategoryState.remove(chatValue.getUserId());
				chatValue.setState(State.DICT_SETTING);
				chatValue.setReplyParseModeHtml();
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
				selectedCategoryState.remove(chatValue.getUserId());
			}
		}
	}

}



