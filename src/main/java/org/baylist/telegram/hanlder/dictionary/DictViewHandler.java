package org.baylist.telegram.hanlder.dictionary;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.PaginationState;
import org.baylist.service.CommonResponseService;
import org.baylist.service.DictionaryService;
import org.baylist.service.MenuService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.LIMIT_CHAR_FOR_ONE_PAGE;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictViewHandler implements DialogHandler {

	CommonResponseService responseService;
	DictionaryService dictionaryService;
	MenuService menuService;
	Map<Long, PaginationState> paginationStateMap = new ConcurrentHashMap<>();


	// state DICT_VIEW
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();

			if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
				paginationStateMap.remove(chatValue.getUserId());
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				menuService.dictionaryMainMenu(chatValue, true);
				paginationStateMap.remove(chatValue.getUserId());
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				Category category = getCategoryFromCallback(chatValue.getUserId(), callbackData);
				handleCategoryChoice(chatValue, category, false);
			} else if (callbackData.equals(Callbacks.DICT_VIEW_PAGINATION_FORWARD.getCallbackData())) {
				updatePagination(chatValue, true);
			} else if (callbackData.equals(Callbacks.DICT_VIEW_PAGINATION_BACK.getCallbackData())) {
				updatePagination(chatValue, false);
			}
		} else {
			menuService.dictionaryMainMenu(chatValue, true);
		}
	}

	public Category getCategoryFromCallback(Long userId, String callbackData) {
		Long categoryId = Long.parseLong(callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length()));
		return dictionaryService.getCategoryByCategoryIdAndUserId(categoryId, userId);
	}

	public void handleCategoryChoice(ChatValue chatValue, Category category, boolean isRemove) {
		category = dictionaryService.getCategoryWithVariants(category.getId());
		List<String> variants = category.getVariants().stream().map(Variant::getName).toList();
		Map<Integer, List<String>> paginate = paginate(variants);
		paginationStateMap.put(chatValue.getUserId(),
				new PaginationState(1, paginate, category.getId(), category.getName()));

		sendPaginatedResponse(chatValue, category.getName(), paginate, 1, isRemove);
	}

	private void updatePagination(ChatValue chatValue, boolean forward) {
		PaginationState state = paginationStateMap.get(chatValue.getUserId());
		if (state != null) {
			int currentPage = state.getCurrentPage();
			int newPage = forward ? currentPage + 1 : currentPage - 1;

			if (newPage >= 1 && newPage <= state.getPages().size()) {
				state.setCurrentPage(newPage);
				sendPaginatedResponse(chatValue, state.getCategoryName(), state.getPages(), newPage, false);
			}
		}
	}

	private void sendPaginatedResponse(ChatValue chatValue,
	                                   String categoryName,
	                                   Map<Integer, List<String>> paginate,
	                                   int currentPage,
	                                   boolean isRemove) {
		StringBuilder sb = new StringBuilder();
		InlineKeyboardMarkup markup = null;
		if (paginate.isEmpty()) {
			sb.append("Вариантов для категории - <b>\"").append(categoryName).append("\"</b> нет");
		} else {
			sb.append("Варианты для категории - ").append(categoryName).append(":\n");
			paginate.get(currentPage).forEach(v -> sb.append("<code>").append(v).append("</code>\n"));
			if (isRemove) {
				sb.append("\n<i>введи список вариантов которые нужно удалить, в столбик. один или больше вариантов из этой категории</i>\n");
			}
			List<InlineKeyboardRow> rows = new LinkedList<>();
			rows.add(new InlineKeyboardRow(List.of(
					InlineKeyboardButton.builder()
							.text("<-")
							.callbackData(Callbacks.DICT_VIEW_PAGINATION_BACK.getCallbackData())
							.build(),
					InlineKeyboardButton.builder()
							.text(currentPage + "/" + paginate.size())
							.callbackData("empty")
							.build(),
					InlineKeyboardButton.builder()
							.text("->")
							.callbackData(Callbacks.DICT_VIEW_PAGINATION_FORWARD.getCallbackData())
							.build()
			)));
			markup = new InlineKeyboardMarkup(rows);
		}
		if (markup == null) {
			markup = new InlineKeyboardMarkup(List.of(
					new InlineKeyboardRow(List.of(
							InlineKeyboardButton.builder()
									.text("назад")
									.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
									.build()))));
		} else {
			markup.getKeyboard().add(new InlineKeyboardRow(List.of(
					InlineKeyboardButton.builder()
							.text("назад")
							.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
							.build())));
		}

		chatValue.setEditText(sb.toString());
		chatValue.setEditReplyParseModeHtml();
		chatValue.setEditReplyKeyboard(markup);
	}

	private Map<Integer, List<String>> paginate(List<String> inputList) {
		var ref = new Object() {
			int currentPageNum = 1;
			int currentLength = 0;
		};
		return new HashMap<>(inputList.stream().collect(Collectors.groupingBy(str -> {
			if (ref.currentLength + str.length() > LIMIT_CHAR_FOR_ONE_PAGE) {
				ref.currentPageNum++;
				ref.currentLength = 0;
			}
			ref.currentLength += str.length();
			return ref.currentPageNum;
		})));
	}

}
