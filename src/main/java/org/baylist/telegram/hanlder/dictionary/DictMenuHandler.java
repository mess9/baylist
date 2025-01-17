package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.service.TgButtonService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictMenuHandler implements DialogHandler {

	CommonResponseService responseService;
	DictionaryService dictionaryService;
	TgButtonService tgButtonService;
	MenuService menuService;


	// state DICT_SETTING
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			Callbacks callback = Callbacks.fromValue(chatValue.getCallbackData());
			switch (callback) {
				case MAIN_MENU -> menuService.mainMenu(chatValue, true);
				case CANCEL -> responseService.cancelMessage(chatValue);
				case DICT_VIEW -> dictView(chatValue);
				case DICT_ADD_CATEGORY -> addCategory(chatValue);
				case DICT_ADD_TASKS_TO_CATEGORY -> addTaskToCategory(chatValue);
				case DICT_REMOVE_CATEGORY -> removeCategory(chatValue);
				case DICT_RENAME_CATEGORY -> renameCategory(chatValue);
				case DICT_REMOVE_VARIANT -> removeVariants(chatValue);
				case DICT_HELP -> responseService.dictHelp(chatValue);
				case DICT_SETTINGS -> menuService.dictionaryMainMenu(chatValue, true);
				default -> chatValue.setState(State.ERROR);
			}
		} else {
			menuService.dictionaryMainMenu(chatValue, false);
		}
	}

	private void dictView(ChatValue chatValue) {
		chatValue.setEditText("внутрь какой категории заглянуть?");
		List<String> categories = dictionaryService.getCategories();
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(categories.stream()
				.map(c -> new InlineKeyboardRow(
						InlineKeyboardButton.builder()
								.text(c)
								.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c)
								.build())).toList());
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setState(State.DICT_VIEW);
	}

	private static void addCategory(ChatValue chatValue) {
		chatValue.setEditText("""
				<b>внимательно слушаю</b>
				 как ты назовёшь <u>новую категорию</u> задач<b>?</b>
				
				<code>- добавить варианты задач, в эту категорию можно будет после её создания</code>
				<code>- название категории должно быть в одну строчку</code>
				<code>- состоять из одного или нескольких слов</code>
				<code>- без спецсимволов</code>
				""");
		chatValue.setEditReplyParseModeHtml();
		chatValue.setState(State.DICT_ADD_CATEGORY);
	}

	private void addTaskToCategory(ChatValue chatValue) {
		chatValue.setEditText("в какую именно категорию добавить варианты задач?");
		tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_ADD_TASK_TO_CATEGORY, true);
	}

	private void removeCategory(ChatValue chatValue) {
		responseService.textChoiceRemoveCategory(chatValue, true);
		tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_REMOVE_CATEGORY, true);
	}

	private void renameCategory(ChatValue chatValue) {
		chatValue.setEditText("какую категорию переименовать?");
		tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_RENAME_CATEGORY, true);
	}

	private void removeVariants(ChatValue chatValue) {
		chatValue.setEditText("выбери категорию, из которой удалить варианты задач");
		tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_REMOVE_VARIANT, true);
	}


}
