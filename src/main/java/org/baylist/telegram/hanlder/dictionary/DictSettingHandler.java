package org.baylist.telegram.hanlder.dictionary;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.service.DictionaryService;
import org.baylist.service.ResponseService;
import org.baylist.service.TgButtonService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@AllArgsConstructor
public class DictSettingHandler implements DialogHandler {

	private ResponseService responseService;
	private DictionaryService dictionaryService;
	private TgButtonService tgButtonService;

	// state DICT_SETTING
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			Callbacks callback = Callbacks.fromValue(chatValue.getCallbackData());
			switch (callback) {
				case CANCEL -> responseService.cancelMessage(chatValue);
				case DICT_VIEW -> {
					chatValue.setReplyText("внутрь какой категории заглянуть?");
					List<String> categories = dictionaryService.getCategories();
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup(categories.stream()
							.map(c -> new InlineKeyboardRow(
									InlineKeyboardButton.builder()
											.text(c)
											.callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData() + c)
											.build())).toList());
					chatValue.setReplyKeyboard(markup);
					chatValue.setState(State.DICT_VIEW);
				}
				case DICT_ADD_CATEGORY -> {
					chatValue.setReplyText("""
							<b>внимательно слушаю</b>
							 как ты назовёшь <u>новую категорию</u> задач<b>?</b>
							
							<code>- добавить варианты задач, в эту категорию можно будет после её создания</code>
							<code>- название категории должно быть в одну строчку</code>
							<code>- состоять из одного или нескольких слов</code>
							<code>- без спецсимволов</code>
							""");
					chatValue.setReplyParseModeHtml();
					chatValue.setState(State.DICT_ADD_CATEGORY);
				}
				case DICT_ADD_TASKS_TO_CATEGORY -> {
					chatValue.setReplyText("в какую именно категорию добавить варианты задач?");
					tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_ADD_TASK_TO_CATEGORY);
				}
				case DICT_REMOVE_CATEGORY -> {
					responseService.textChoiceRemoveCategory(chatValue, false);
					tgButtonService.setCategoriesChoiceKeyboard(chatValue, State.DICT_REMOVE_CATEGORY);
				}
				case DICT_HELP -> {
					chatValue.setReplyText("""
							<i><b>категории</b> и <b>варианты</b>, это мой <u>внутренний словарик</u> для того что бы я мог раскидать список вводимых тобой задач, по категориям в проекте todoist.</i>
							<i>категория не появятся в проекте todoist до тех пор пока я не отправлю в неё задачку.</i>
							<i>а чтобы задачка туда отправилась, я должен про неё знать. для этого мы сейчас и заполняем этот словарик</i>
							
							<b>пример использования:</b>
							в словарике есть 2 категории
							категория 1 - <i>продукты</i>.
							категория 2 - <i>пункты выдачи</i>.
							в <i>продуктах</i> есть варианты -> <code>помидоры, картошка, морковка</code>
							в <i>пунктах выдачи</i> -> <code>вб, озон, почта</code>
							
							если ты отправишь мне сообщение из двух строк
							<code>морковка</code>
							<code>вб</code>
							
							то в проекте todoist - появится 2 категории, и в каждой по одной задаче
							""");
					chatValue.setReplyParseModeHtml();
					chatValue.setState(State.DICT_SETTING);
					chatValue.setReplyKeyboard(new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(
							InlineKeyboardButton.builder()
									.text("ok")
									.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
									.build()))));
				}
				case DICT_SETTINGS -> dictionaryService.settingsMainMenu(chatValue);
				default -> chatValue.setState(State.ERROR);
			}

		} else {
			dictionaryService.settingsMainMenu(chatValue);
		}
	}


}
