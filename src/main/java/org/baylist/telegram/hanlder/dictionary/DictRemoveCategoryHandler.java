package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.ResponseService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@AllArgsConstructor
public class DictRemoveCategoryHandler implements DialogHandler {

	private DictionaryService dictionaryService;
	private ResponseService responseService;


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
				chatValue.setReplyText("удалить категорию - " + category + " ?");
				chatValue.setState(State.DICT_REMOVE_CATEGORY);
				chatValue.getUser().getDialog().setSelectedCategory(category);
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
						new InlineKeyboardRow(List.of(InlineKeyboardButton.builder()
										.text("йеп")
										.callbackData(Callbacks.REMOVE_CATEGORY.getCallbackData())
										.build(),
								InlineKeyboardButton.builder()
										.text("неа")
										.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
										.build()))));
				chatValue.setReplyKeyboard(markup);
			} else if (callbackData.startsWith(Callbacks.REMOVE_CATEGORY.getCallbackData())) {
				String category = chatValue.getUser().getDialog().getSelectedCategory();
				dictionaryService.removeCategory(category);
				dictionaryService.settingsShortMenu(chatValue, "категория - [ <b>" + category + "</b> ] - удалена");
				chatValue.setState(State.DICT_SETTING);
				chatValue.setReplyParseModeHtml();
			}
		}
	}
}

// переделать на такой режим
// 1. получаем клавиатуру из категорий
// 2. тыкаем на любую из них
// 3. получаем ту же клавиатуру с категориями, но теперь одна из них выбрана чекбоксом + появились кнопки удалить и назад
// 4. можно выбрать ещё несколько категорий
// 5. нажимаем удалить - и выбранные категории удаляются (прихранять в мапу)
// 6. подтверждение, да/нет
// 7. нажимаем назад - возвращаемся в настройки словарика
// выравнивание категорий с чекбоксами (чем длиннее категория, тем меньше пробелов слева, до чекбокса



