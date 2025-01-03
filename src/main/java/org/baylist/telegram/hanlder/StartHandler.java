package org.baylist.telegram.hanlder;

import lombok.AllArgsConstructor;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.baylist.service.TodoistService;
import org.baylist.service.UserService;
import org.baylist.telegram.hanlder.config.DialogHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class StartHandler implements DialogHandler {

	private UserService userService;
	private TodoistService todoistService;

	// state START
	@Override
	public void handle(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			String callbackData = chatValue.getCallbackData();
			if (callbackData.equals(Callbacks.START.getCallbackData())) {
				todoistAnswer(chatValue);
			} else if (callbackData.equals(Callbacks.TODOIST_TOKEN.getCallbackData())) {
				tokenRequest(chatValue);
			} else if (callbackData.equals(Callbacks.WITHOUT_TODOIST_TOKEN.getCallbackData())) {
				friendsAnswer(chatValue);
			} else if (callbackData.equals(Callbacks.ADD_FRIENDS.getCallbackData())) {
				friendsRequest(chatValue);
			} else if (callbackData.equals(Callbacks.NO_FRIENDS.getCallbackData())) {
				done(chatValue);
			}

		} else {
			tokenResponse(chatValue);
		}
	}

	private void todoistAnswer(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> первый этап настройки </b>
				планируется только отправка задач другу, или таки зарегистрируемся в todoist?
				
				<i>добавить токен todoist можно будет позже</i>
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("да(полный функционал)")
								.callbackData(Callbacks.TODOIST_TOKEN.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("неа, у меня лапки")
								.callbackData(Callbacks.WITHOUT_TODOIST_TOKEN.getCallbackData())
								.build())
				));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
	}

	private void tokenRequest(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> как получить токен </b>
				0. регистрируемся на todoist.com
				1. переходим по ссылке https://todoist.com/prefs/integrations
				2. переключаемся на вкладку "для разработчиков"
				3. копируем токен и отправляем его боту
				""");
		chatValue.setReplyParseModeHtml();
	}

	private void friendsAnswer(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> второй этап настройки </b>
				добавить друзей, или ну их всех?
				
				<i>это можно будет сделать позже</i>
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("добавить")
								.callbackData(Callbacks.ADD_FRIENDS.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("та ну их")
								.callbackData(Callbacks.NO_FRIENDS.getCallbackData())
								.build())
				));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
	}

	private void friendsRequest(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> добавление друзей </b>
				пока что это не реализовано, но скоро будет
				"""); //todo добавление друзей
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.DEFAULT);
	}

	private void done(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> настройка завершена </b>
				""");
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.DEFAULT);
	}

	private void tokenResponse(ChatValue chatValue) {
		String inputText = chatValue.getInputText();
		String regex = "[0-9a-f]{40}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(inputText);
		if (inputText.equals(Commands.START.getCommand())) {
			start(chatValue);
		} else if (matcher.matches() && inputText.length() == 40) {
			userService.saveToken(chatValue, inputText);
			todoistService.createProject(chatValue);
			InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
					.keyboard(List.of(
							new InlineKeyboardRow(List.of(
									InlineKeyboardButton.builder()
											.text("добавить друзей(можно будет сделать позже)")
											.callbackData(Callbacks.ADD_FRIENDS.getCallbackData())
											.build())),
							new InlineKeyboardRow(List.of(
									InlineKeyboardButton.builder()
											.text("завершить настройку")
											.callbackData(Callbacks.NO_FRIENDS.getCallbackData())
											.build()))))
					.build();
			chatValue.setReplyText("""
					токен получен, спасибо
					
					в todoist будет создан проект "baylist"
					куда и будут отправляться задачи от вас и ваших друзей
					(если они у вас есть)
					
					проект будет создан даже если лимит по проектам закончился
					это такая api магия
					""");
			chatValue.setReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("""
					неверный токен, попробуйте ещё раз
					
					или воспользуйтесь командой /report дабы описать что именно пошло не так.
					""");
		}
	}

	private void start(ChatValue chatValue) {
		chatValue.setReplyText("""
				yay! приветствую.
				этот бот написан филом, что бы отправлять ему список покупок
				
				основная идея такова. писать боту в телеграме и он бы добавлял задачи в todoist
				
				такая вот автоматизация пользовательского опыта.
				раньше я накидывал в избранное в тг список дел и покупок перед выходом из дома,
				чтобы ничего не забыть. но это имеет свои минусы, например нельзя отмечать уже сделанное/купленное, дабы оно не мозолило глаз,
				и приходится распределять задачи по категориям/местам вручную, или придётся видеть перед собой плоский не структурированный список.
				
				бот умеет принимать в себя список дел/покупок, разбивать их по категориям и отправлять в todoist.
				
				категории и то, что в них попадает, полностью настраивается пользователем.
				
				так же возможно и совместное использование бота
				ваши друзья/семья могут накидывать вам в этого бота что-то, дабы не забыть это купить/сделать.
				так что ботом можно пользоваться как в роли реципиента, так и в роли отправителя задач, так и в обеих ролях одновременно.
				
				для того чтобы мочь получать задачи от себя или друзей, нужно зарегистрироваться в todoist и получить там токен.
				(инструкция по получению токена будет чуть позже)
				(позже планируется уйти от todoist или сделать его опциональным, но пока так, он неплох)
				
				а теперь этап первоначальной настройки.
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("начать настройку")
						.callbackData(Callbacks.START.getCallbackData())
						.build())));
		chatValue.setReplyKeyboard(markup);
		chatValue.setState(State.START);
	}
}
