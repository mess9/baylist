package org.baylist.service;

import lombok.AllArgsConstructor;
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
		List<String> categories = dictionaryService.getCategories();
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(categories.stream()
				.map(c -> new InlineKeyboardRow(
						InlineKeyboardButton.builder()
								.text(c)
								.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c)
								.build())).toList());
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
				.peek(c -> maxLength.set(Math.max(maxLength.get(), c.length())))
				.map(c -> {
					String invisibleSpace = "⠀";
					String leftPadding = "☐ - ";
					if (selectedCategoryState.getSelectedCategories().contains(c)) {
						leftPadding = "☑ - ";
					}

					int paddingSize = maxLength.get() - c.length();
					String paddedText = leftPadding + c + (invisibleSpace.repeat(paddingSize));

					return new InlineKeyboardRow(
							InlineKeyboardButton.builder()
									.text(paddedText)
									.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c)
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
