package org.baylist.service;

import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;

@Component
public class ResponseService {

	//тут будет форматирование и локализация ответа

	public void cancelMessage(ChatValue chatValue) {
		chatValue.setReplyText("ок. в следующий раз будут деяния. а пока я отдохну");
		chatValue.setState(State.DEFAULT);
	}

	public void textChoiceRemoveCategory(ChatValue chatValue, boolean isEdit) {
		if (isEdit) {
			chatValue.setEditMessage("""
					какие категории удалить?
					
					<i>примечание, вместе с категорией удаляются и все связанные с ней варианты задач</i>
					""");
			chatValue.setEditReplyParseModeHtml();
		} else {
			chatValue.setReplyText("""
					какие категории удалить?
					
					<i>примечание, вместе с категорией удаляются и все связанные с ней варианты задач</i>
					""");
			chatValue.setReplyParseModeHtml();
		}
	}

	public void textChoiceRemoveVariant(ChatValue chatValue, boolean success) {
		if (success) {
			chatValue.setReplyText("введённые варианты были успешно удалены");
		} else {
			chatValue.setReplyText("введённые варианты не были удалены. попробуйте ещё раз" +
					" но как-то так что бы сработало");
		}
	}
}
