package org.baylist.telegram;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatState;
import org.baylist.service.DictionaryService;
import org.baylist.service.TodoistService;
import org.baylist.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static org.baylist.util.log.TgLog.inputLogButton;

@Component
@AllArgsConstructor
public class Button {

	private TodoistService todoist;
	private UserService userService;
	private DictionaryService dictionaryService;

	//ограничения полученные опытным путём
	// максимальное количество кнопок в одной строке - 8
	// - в название влезает 4 символа
	// - 3 символа если с телефона
	// максимальное в высоту - хз, пробовал 20, 20 влезает.
	// - в название влезает 56 символов
	// - 41 символ если с телефона

	public void buttons(ChatState chatState) {
		String data = chatState.getCallbackData();
		inputLogButton(chatState.getUpdate());

		if (data.equals(Callbacks.CANCEL.getCallbackData())) {
			cancel(chatState);
		} else if (data.equals(Callbacks.APPROVE.getCallbackData())) {
			approve(chatState);
		} else if (data.equals(Callbacks.VIEW.getCallbackData())) {
			view(chatState);
		} else if (data.equals(Callbacks.DONATE.getCallbackData())) {
			donate(chatState);
		} else if (data.equals(Callbacks.FEEDBACK.getCallbackData())) {
			feedback(chatState);
		} else if (data.equals(Callbacks.ADD_CATEGORY.getCallbackData())) {
			addCategory(chatState);
		} else if (data.equals(Callbacks.ADD_TASKS_TO_CATEGORY.getCallbackData())) {
			choiceCategoryForAddTaskToCategory(chatState);
		} else if (data.contains(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
			addTasksToCategory(chatState);
		}

	}

	private void cancel(ChatState chatState) {
		chatState.setReplyText("ок. в следующий раз будут деяния. а пока я отдохну");
		userService.addCategoryOff(chatState);
		userService.feedbackOff(chatState);
	}

	private void approve(ChatState chatState) {
		chatState.setReplyText(todoist.clearBuyList());
	}

	private void view(ChatState chatState) {
		chatState.setReplyText(todoist.getBuylistProject());
		chatState.setReplyParseModeHtml();
	}

	private void donate(ChatState chatState) {
		chatState.setReplyText("""
				спасибо за нажатие на эту кнопку!
				
				приму любого размера помощь (до 1G$)
				💳 (mastercard)
				4454 3000 0304 4598
				₿ (bitcoin)
				bc1qdgnwxpjtfhqztw6thq3yukcddrpms48wk4dhy0
				""");
	}

	private void feedback(ChatState chatState) {
		chatState.setReplyText("я вас внимательно слушаю");
		userService.feedbackOn(chatState);
	}

	private void addCategory(ChatState chatState) {
		chatState.setReplyText("пиши название категории - я всё запомню");
		userService.addCategoryOn(chatState);
	}

	private void choiceCategoryForAddTaskToCategory(ChatState chatState) {
		chatState.setReplyText("в какую категорию добавить задачи?");
		List<String> categories = dictionaryService.getCategories();
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(categories.stream()
				.map(c -> new InlineKeyboardRow(
						InlineKeyboardButton.builder()
								.text(c)
								.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c)
								.build())).toList());
		chatState.setReplyKeyboard(markup);

		userService.addCategoryOn(chatState);
	}

	private void addTasksToCategory(ChatState chatState) {
		String category = chatState.getCallbackData().substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length());
		chatState.setReplyText("""
				добавляйте задачи в категорию - %s
				
				просто вводите задачи в столбик, одну или больше
				<code>- название задачи должно быть в одну строчку</code>
				<code>- состоять из одного или нескольких слов</code>
				<code>- без спецсимволов</code>
				""".formatted(category));
		chatState.setReplyParseModeHtml();
//		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("добавить ещё категории")
//						.callbackData(Callbacks.ADD_CATEGORY.getCallbackData())
//						.build()),
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("добавить задачи в ту же категорию?")
//						.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + category)
//						.build()),
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("выбрать другую категорию для добавления задач")
//						.callbackData(Callbacks.ADD_TASKS_TO_CATEGORY.getCallbackData())
//						.build()),
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("показать что сейчас есть в словарике")
//						.callbackData(Callbacks.VIEW_CATEGORIES.getCallbackData())
//						.build()),
//				new InlineKeyboardRow(InlineKeyboardButton.builder()
//						.text("фсё, пока хватит")
//						.callbackData(Callbacks.CANCEL.getCallbackData())
//						.build())
//		));


		userService.addCategoryOn(chatState);
		userService.addCategoryContext(chatState.getUser(), category);
	}
}

