//package org.baylist.telegram;
//
//import lombok.AllArgsConstructor;
//import org.baylist.dto.telegram.Callbacks;
//import org.baylist.dto.telegram.ChatValue;
//import org.baylist.dto.telegram.Commands;
//import org.baylist.service.TodoistService;
//import org.baylist.service.UserService;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@AllArgsConstructor
//public class Command {
//
//	private TodoistService todoist;
//	private UserService userService;
//
//	public void commandHandler(ChatValue chatValue) {
//		String updateMessage = chatValue.getUpdate().getMessage().getText();
//		if (Arrays.stream(Commands.values()).anyMatch(c -> c.getCommand().equals(updateMessage))) {
//			if (updateMessage.equals(Commands.CLEAR.getCommand())) {
//				clear(chatValue);
//			} else if (updateMessage.equals(Commands.VIEW.getCommand())) {
//				view(chatValue);
//			}  else if (updateMessage.equals(Commands.START.getCommand())) {
//				start(chatValue);
//			} else if (updateMessage.equals(Commands.REPORT.getCommand())) {
//				report(chatValue);
//			} else if (updateMessage.equals(Commands.ADD_CATEGORY.getCommand())) {
//				startAddCategoryDialog(chatValue);
//			}
//		}
//	}
//
//	private void clear(ChatValue chatValue) {
//		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
//				new InlineKeyboardRow(
//						InlineKeyboardButton.builder()
//								.text("СЖЕЧЬ ИХ ВСЕХ!")
//								.callbackData(Callbacks.APPROVE.getCallbackData())
//								.build(),
//						InlineKeyboardButton.builder()
//								.text("неа, не надо")
//								.callbackData(Callbacks.CANCEL.getCallbackData())
//								.build())));
//		chatValue.setReplyText("удалить вообще все незакрытые задачи?");
//		chatValue.setReplyKeyboard(markup);
//	}
//
//	private void view(ChatValue chatValue) {
//		chatValue.setReplyText(todoist.getBuylistProject());
//		chatValue.setReplyParseModeHtml();
//		chatValue.setCommandProcess(true);
//	}
//
//	private void sync(ChatValue chatValue) {
//		todoist.syncBuyListData();
//		chatValue.setReplyText("данные синхронизированы с todoist");
//		chatValue.setCommandProcess(true);
//	}
//
//	private void start(ChatValue chatValue) {
//		chatValue.setReplyText("""
//				yay!, приветствую.
//				этот бот написал филом, что бы отправлять ему список покупок
//
//				если вы не знаете кто такой фил, вы явно обратились к этому боту по ошибке,
//				но всё равно будьте вы счастливы, но только если вы хороший человек,
//				если не хороший, например путин или что-то похожее, то пожалуйста убей себя каким нибудь болезненным и публичным способом.
//				 ня)
//				с любовью. фил.
//				""");
//		chatValue.setCommandProcess(true);
//	}
//
//	private void report(ChatValue chatValue) {
//		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
//				new InlineKeyboardRow(
//						InlineKeyboardButton.builder()
//								.text("take my money!")
//								.callbackData(Callbacks.DONATE.getCallbackData())
//								.build(),
//						InlineKeyboardButton.builder()
//								.text("feedback")
//								.callbackData(Callbacks.FEEDBACK.getCallbackData())
//								.build())));
//		chatValue.setReplyText("""
//				тут можно оставить обратную связь по работе данного бота
//				принимаются:
//				 - донаты
//				 - баг репорты
//				 - слова благодарности
//				 - конструктивная критика
//				 - смешные мемы
//				""");
//		chatValue.setReplyKeyboard(markup);
//		chatValue.setCommandProcess(true);
//	}
//
//	private void startAddCategoryDialog(ChatValue chatValue) {
//		chatValue.setReplyText("""
//				<i><b>примечание:</b> категории и варианты, это мой <u>внутренний словарик</u> для того что бы я мог раскидать список вводимых тобой задач, по категориям в проекте todoist. категория не появятся в проекте todoist до тех пор пока я не отправлю в неё задачку.</i>
//				<i>а чтобы задачка туда отправилась, я должен про неё знать. для этого мы сейчас и заполняем этот словарик</i>
//
//				<u>внимательно слушаю - как ты назовёшь категорию задач?</u>
//
//				<code>- добавить варианты задач, в эту категорию можно будет после её создания</code>
//				<code>- название категории должно быть в одну строчку</code>
//				<code>- состоять из одного или нескольких слов</code>
//				<code>- без спецсимволов</code>
//				""");
//		chatValue.setCommandProcess(true);
//		chatValue.setReplyParseModeHtml();
//		userService.addCategoryOn(chatValue);
//	}
//
//}
