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
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
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
			} else if (callbackData.equals(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())) {
				if (userService.isExistToken(chatValue.getUser().getUserId())) {
					existToken(chatValue);
				} else {
					tokenRequest(chatValue);
				}
			} else if (callbackData.equals(Callbacks.START_1_TODOIST_TOKEN_CHANGE.getCallbackData())) {
				tokenRequest(chatValue);
			} else if (callbackData.equals(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())) {
				if (userService.isExistToken(chatValue.getUser().getUserId())) {
					friendsAnswer(chatValue);
				} else {
					doneWithouFriends(chatValue);
				}
			} else if (callbackData.equals(Callbacks.ADD_FRIENDS.getCallbackData())) {
				friendsRequest(chatValue);
			} else if (callbackData.equals(Callbacks.NO_FRIENDS.getCallbackData())) {
				done(chatValue);
			}

		} else {
			Update update = chatValue.getUpdate();
			if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(Commands.START.getCommand())) {
				start(chatValue);
			} else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 40) {
				tokenResponse(chatValue);
			} else if (update.hasMessage() && update.getMessage().hasContact()) {
				Contact contact = update.getMessage().getContact();
				if (userService.addFriend(chatValue, contact)) {
					chatValue.setReplyText("друг добавлен");
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
							List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
											.text("добавить ещё")
											.callbackData(Callbacks.ADD_FRIENDS.getCallbackData())
											.build()),
									new InlineKeyboardRow(InlineKeyboardButton.builder()
											.text("друзья кончились")
											.callbackData(Callbacks.NO_FRIENDS.getCallbackData())
											.build())
							));
					chatValue.setReplyKeyboard(markup);
				} else {
					chatValue.setReplyText("такой друг у тебя уже есть, давай другого");
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
							List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
											.text("добавить ещё")
											.callbackData(Callbacks.ADD_FRIENDS.getCallbackData())
											.build()),
									new InlineKeyboardRow(InlineKeyboardButton.builder()
											.text("друзья закончились")
											.callbackData(Callbacks.NO_FRIENDS.getCallbackData())
											.build())
							));
					chatValue.setReplyKeyboard(markup);
				}
			} else {
				chatValue.setReplyText("не понимаю");
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
						List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("давай всё заново")
								.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
								.build())
						));
				chatValue.setReplyKeyboard(markup);
			}
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
								.text("да (добавить/изменить токен)")
								.callbackData(Callbacks.START_1_TODOIST_TOKEN_REQUEST.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("пропустить пока")
								.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
								.build())
				));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
	}

	private static void existToken(ChatValue chatValue) {
		chatValue.setEditText("токен уже добавлен");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("изменить привязанный токен")
								.callbackData(Callbacks.START_1_TODOIST_TOKEN_CHANGE.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("дальше")
								.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
								.build())
				));
		chatValue.setEditReplyKeyboard(markup);
	}

	private void tokenRequest(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> как получить токен </b>
				0. регистрируемся на todoist.com
				1. переходим по ссылке https://todoist.com/prefs/integrations
				2. переключаемся на вкладку "для разработчиков"
				3. копируем токен и отправляем его боту (вставить в чат, в ответ на это сообщение)
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

	private void doneWithouFriends(ChatValue chatValue) {
		chatValue.setReplyText("""
				<i> если бы у вас был токен todoist вы бы могли добавить друзей которые могут отправлять вам задачи </i>
				
				но у вас его нет
				так что бот для вас будет бесполезен до тех пор, пока ваш друг не добавит вас в к себе в профиль, что бы вы могли отправлять ему задачи
				"""); //todo добавить проверочку на наличие подходящих друзей, с отправкой им уведомлений
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.DEFAULT);
	}

	private void friendsRequest(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> добавление друзей </b>
				
				пришлите мне контакт вашего друга, который сможет отправлять вам задачи
				""");
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.START);
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
				(позже планируется уйти от todoist или сделать его опциональным, но пока <i>и так сойдёт</i>, todoist вполне неплох)
				
				а теперь этап первоначальной настройки.
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("начать настройку")
						.callbackData(Callbacks.START.getCallbackData())
						.build())));
		chatValue.setReplyKeyboard(markup);
		chatValue.setReplyParseModeHtml();
		chatValue.setState(State.START);
	}
}
