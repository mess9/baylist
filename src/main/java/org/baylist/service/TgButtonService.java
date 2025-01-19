package org.baylist.service;

import lombok.AllArgsConstructor;
import org.baylist.db.entity.Category;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.SelectedCategoryState;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TgButtonService {

	private final DictionaryService dictionaryService;


	public void setCategoriesChoiceKeyboard(ChatValue chatValue, State state, boolean isEdit) {
		List<Category> categories = dictionaryService.getCategoriesByUserId(chatValue.getUserId());
		List<InlineKeyboardRow> rows = categories.stream()
				.map(c -> new InlineKeyboardRow(
						InlineKeyboardButton.builder()
								.text(c.getName())
								.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c.getId())
								.build())).collect(Collectors.toList());
		rows.add(new InlineKeyboardRow(
				InlineKeyboardButton.builder()
						.text("⏪ назад")
						.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
						.build()));
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
		if (isEdit) {
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyKeyboard(markup);
		}
		chatValue.setState(state);
	}

	public void categoriesChoiceKeyboardEdit(ChatValue chatValue, State state,
	                                         SelectedCategoryState selectedCategoryState) {
		AtomicInteger maxLength = new AtomicInteger();
		List<InlineKeyboardRow> categoryButtons = selectedCategoryState.getCategories().stream()
				.peek(c -> maxLength.set(Math.max(maxLength.get(), c.getName().length())))
				.map(c -> {
					String invisibleSpace = "⠀";
					String leftPadding = "\uD83D\uDFE9 - ";
					if (selectedCategoryState.getSelectedCategories().contains(c)) {
						leftPadding = "✅ - ";
					}

					int paddingSize = maxLength.get() - c.getName().length();
					String paddedText = leftPadding + c.getName() + (invisibleSpace.repeat(paddingSize));

					return new InlineKeyboardRow(
							InlineKeyboardButton.builder()
									.text(paddedText)
									.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c.getId())
									.build());
				})
				.collect(Collectors.toList());
		categoryButtons.add(new InlineKeyboardRow(List.of(InlineKeyboardButton.builder()
						.text("сжечь!")
						.callbackData(Callbacks.REMOVE_CATEGORY.getCallbackData())
						.build(),
				InlineKeyboardButton.builder()
						.text("пощадить")
						.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
						.build())));
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(categoryButtons);
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setState(state);
	}


}
