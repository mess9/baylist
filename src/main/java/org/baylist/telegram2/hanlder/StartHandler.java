package org.baylist.telegram2.hanlder;

import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.baylist.telegram2.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements DialogHandler {

	@Override
	public void handle(ChatValue chatValue) {
		chatValue.setReplyText("""
				yay!, приветствую.
				этот бот написал филом, что бы отправлять ему список покупок
				
				если вы не знаете кто такой фил, вы явно обратились к этому боту по ошибке,
				но всё равно будьте вы счастливы, но только если вы хороший человек,
				если не хороший, например путин или что-то похожее, то пожалуйста убей себя каким нибудь болезненным и публичным способом.
				 ня)
				с любовью. фил.
				""");
		chatValue.setState(State.DEFAULT); //todo переделать на первоначальную настройку
	}
}
