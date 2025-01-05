package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.PaginationState;
import org.baylist.service.DictionaryService;
import org.baylist.service.ResponseService;
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
@AllArgsConstructor
public class DictViewHandler implements DialogHandler {

	private ResponseService responseService;
	private DictionaryService dictionaryService;
	private final Map<Long, PaginationState> paginationStateMap = new ConcurrentHashMap<>();


	// state DICT_VIEW
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();

			if (callbackData.equals(Callbacks.CANCEL.getCallbackData())) {
				responseService.cancelMessage(chatValue);
				paginationStateMap.remove(chatValue.getUser().getUserId());
			} else if (callbackData.equals(Callbacks.DICT_SETTINGS.getCallbackData())) {
				dictionaryService.settingsMainMenu(chatValue, true);
				paginationStateMap.remove(chatValue.getUser().getUserId());
			} else if (callbackData.startsWith(Callbacks.CATEGORY_CHOICE.getCallbackData())) {
				handleCategoryChoice(chatValue, callbackData);
			} else if (callbackData.equals(Callbacks.DICT_VIEW_PAGINATION_FORWARD.getCallbackData())) {
				updatePagination(chatValue, true);
			} else if (callbackData.equals(Callbacks.DICT_VIEW_PAGINATION_BACK.getCallbackData())) {
				updatePagination(chatValue, false);
			}
		} else {
			dictionaryService.settingsMainMenu(chatValue, true);
		}
	}

	public void handleCategoryChoice(ChatValue chatValue, String callbackData) {
		String categoryName = callbackData.substring(Callbacks.CATEGORY_CHOICE.getCallbackData().length());
		List<String> variants = dictionaryService.getVariants(categoryName);
		Map<Integer, List<String>> paginate = paginate(variants);

		paginationStateMap.put(chatValue.getUser().getUserId(), new PaginationState(1, paginate, categoryName));

		sendPaginatedResponse(chatValue, categoryName, paginate, 1);
	}

	private void updatePagination(ChatValue chatValue, boolean forward) {
		PaginationState state = paginationStateMap.get(chatValue.getUser().getUserId());
		if (state != null) {
			int currentPage = state.getCurrentPage();
			int newPage = forward ? currentPage + 1 : currentPage - 1;

			if (newPage >= 1 && newPage <= state.getPages().size()) {
				state.setCurrentPage(newPage);
				sendPaginatedResponse(chatValue, state.getCategoryName(), state.getPages(), newPage);
			}
		}
	}

	private void sendPaginatedResponse(ChatValue chatValue,
	                                   String categoryName,
	                                   Map<Integer, List<String>> paginate,
	                                   int currentPage) {
		StringBuilder sb = new StringBuilder();
		InlineKeyboardMarkup markup = null;
		if (paginate.isEmpty()) {
			sb.append("Вариантов для категории - <b>\"").append(categoryName).append("\"</b> нет");
		} else {
			sb.append("Варианты для категории - ").append(categoryName).append(":\n");
			paginate.get(currentPage).forEach(v -> sb.append("<code>").append(v).append("</code>\n"));
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
