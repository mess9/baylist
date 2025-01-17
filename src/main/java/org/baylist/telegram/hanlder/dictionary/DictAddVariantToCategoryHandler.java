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
public class DictAddVariantToCategoryHandler implements DialogHandler {

	DictionaryService dictionaryService;
	CommonResponseService responseService;
	MenuService menuService;

	//todo валидация на уникальность вариантов среди всех категорий пользователя

	// state DICT_ADD_TASK_TO_CATEGORY
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				String category = callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length());
				chatValue.setEditText("""
						добавляйте варианты задач в категорию - %s
						
						просто вводите их в столбик, один, два или больше
						
						<code>- название задачи должно быть в одну строчку</code>
						<code>- состоять из одного или нескольких слов</code>
						<code>- без спецсимволов</code>
						""".formatted(category));
				chatValue.setEditReplyParseModeHtml();
				chatValue.setState(State.DICT_ADD_TASK_TO_CATEGORY);
				chatValue.getUser().getDialog().setSelectedCategory(category);
			}
		} else {
			String selectedCategory = chatValue.getUser().getDialog().getSelectedCategory();
			if (selectedCategory != null) {
				dictionaryService.addVariantToCategory(chatValue, selectedCategory);
			} else {
				chatValue.setState(State.ERROR);
			}
		}
	}


}
