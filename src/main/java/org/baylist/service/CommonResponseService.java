package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommonResponseService {

	TodoistService todoistService;
	UserService userService;
	DictionaryService dictionaryService;

	//тут будет форматирование и локализация ответа

	public void cancelMessage(ChatValue chatValue) {
		chatValue.setReplyText("ок. в следующий раз будут деяния. а пока я отдохну");
		chatValue.setState(State.DEFAULT);
	}

	public void textChoiceRemoveCategory(ChatValue chatValue, boolean isEdit) {
		if (isEdit) {
			chatValue.setEditText("""
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

	public void view(ChatValue chatValue, boolean isMenu) {
		if (isMenu) {
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
					new InlineKeyboardRow(InlineKeyboardButton.builder()
							.text("назад")
							.callbackData(Callbacks.MAIN_MENU.getCallbackData())
							.build())));
			chatValue.setEditText(todoistService.getBuylistProject());
			chatValue.setEditReplyParseModeHtml();
			chatValue.setEditReplyKeyboard(markup);
			chatValue.setState(State.MENU);
		} else {
			chatValue.setReplyText(todoistService.getBuylistProject());
			chatValue.setReplyParseModeHtml();
			chatValue.setState(State.DEFAULT);
		}


	}

	@Transactional
	public void info(ChatValue chatValue) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
				new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("назад")
						.callbackData(Callbacks.MAIN_MENU.getCallbackData())
						.build())
		));

		StringBuilder sb = new StringBuilder();
		boolean existToken = userService.isExistToken(chatValue.getUser().getUserId());
		User user = userService.getUserFromDb(chatValue.getUser().getUserId());
		List<Category> categoriesByUserId = dictionaryService.getCategoriesByUserId(user.getUserId());
		long variants = 0;
		if (!categoriesByUserId.isEmpty()) {
			variants = categoriesByUserId.stream()
					.filter(c -> !c.getVariants().isEmpty())
					.mapToLong(c -> c.getVariants().size())
					.sum();
		}
		List<User> friends = user.getFriends();
		List<User> friendList = userService.getFriendList(user.getUserId());


		sb.append("<b>сводная информация:</b>\n")
				.append("вас зовут - ")
				.append(user.getFirstName())
				.append(" ")
				.append(user.getLastName())
				.append("\n\n");
		if (existToken) {
			sb.append("вы подключены к todoist\n\n");
			if (!categoriesByUserId.isEmpty()) {
				sb.append("вы создали категорий задач - ").append(categoriesByUserId.size()).append("\n");
				categoriesByUserId.forEach(c -> sb.append(" <code>").append(c.getName()).append("</code>\n"));
				if (variants > 0) {
					sb.append("<i>вы создали вариантов задач - </i>").append(variants).append("\n\n");
				} else {
					sb.append("<i>вы не создали вариантов задач в ваших категориях </i> <b>:(</b>\n\n");
				}
			} else {
				sb.append("<i>вы не создали категорий задач </i> <b>:(</b>\n\n");
			}
			if (!friends.isEmpty()) {
				if (friends.size() > 1) {
					sb.append("у вас есть друзья:\n");
					friends.forEach(f -> sb.append(" <code>")
							.append(f.getFirstName())
							.append(" ")
							.append(f.getLastName())
							.append("</code>\n"));
				} else {
					sb.append("у вас есть друг!\n");
					friends.forEach(f -> sb.append(" <code>")
							.append(f.getFirstName())
							.append(" ")
							.append(f.getLastName())
							.append("</code>\n"));
				}
			} else {
				sb.append("у вас нет друзей\n");
			}
		} else {
			if (friendList.isEmpty()) {
				sb.append("""
						вы не подключены к todoist
						
						и у вас нет друзей
						которые бы внесли бы вас в свой список отправителей им задач
						для вас этот бот
						пока что
						совершенно бесполезен
						<b>:(</b>
						""");
			} else {
				sb.append("вы не подключены к todoist\nно у вас есть ");
				if (friendList.size() > 1) {
					sb.append("друзья, которым вы можете отправлять задачи\n");
					friendList.forEach(f -> sb.append(" <code>")
							.append(f.getFirstName())
							.append(" ")
							.append(f.getLastName())
							.append("</code>\n"));
				} else {
					sb.append("друг, которому вы можете отправлять задачи\n");
					friendList.forEach(f -> sb.append(" <code>")
							.append(f.getFirstName())
							.append(" ")
							.append(f.getLastName())
							.append("</code>\n"));
				}
			}
		}

		chatValue.setEditText(sb.toString());
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
		chatValue.setState(State.MENU);
	}

	public void existToken(ChatValue chatValue) {
		chatValue.setEditText("токен уже добавлен");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("изменить привязанный токен")
								.callbackData(Callbacks.START_1_TODOIST_TOKEN_CHANGE.getCallbackData())
								.build()),
						new InlineKeyboardRow(InlineKeyboardButton.builder()
								.text("неа")
								.callbackData(Callbacks.START_2_FRIENDS_REQUEST.getCallbackData())
								.build())
				));
		chatValue.setEditReplyKeyboard(markup);
	}

	public void tokenRequest(ChatValue chatValue) {
		chatValue.setReplyText("""
				<b> как получить токен </b>
				0. регистрируемся на todoist.com
				1. переходим по ссылке https://todoist.com/prefs/integrations
				2. переключаемся на вкладку "для разработчиков"
				3. копируем токен и отправляем его боту (вставить в чат, в ответ на это сообщение)
				""");
		chatValue.setReplyParseModeHtml();
	}

	public void tokenResponse(ChatValue chatValue, boolean isStart) {
		String inputText = chatValue.getInputText();
		String regex = "[0-9a-f]{40}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(inputText);
		if (inputText.equals(Commands.START.getCommand())) {
			start(chatValue);
		} else if (matcher.matches() && inputText.length() == 40) {
			userService.saveToken(chatValue, inputText);
			todoistService.createProject(chatValue);
			InlineKeyboardMarkup markup;
			if (isStart) {
				markup = InlineKeyboardMarkup.builder()
						.keyboard(List.of(
								new InlineKeyboardRow(List.of(
										InlineKeyboardButton.builder()
												.text("добавить друзей(можно будет сделать позже)")
												.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
												.build())),
								new InlineKeyboardRow(List.of(
										InlineKeyboardButton.builder()
												.text("завершить настройку")
												.callbackData(Callbacks.START_DONE.getCallbackData())
												.build()))))
						.build();
			} else {
				markup = InlineKeyboardMarkup.builder()
						.keyboard(List.of(
								new InlineKeyboardRow(List.of(
										InlineKeyboardButton.builder()
												.text("yay!")
												.callbackData(Callbacks.MAIN_MENU.getCallbackData())
												.build()))))
						.build();
				chatValue.setState(State.MENU);
			}

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

	public void start(ChatValue chatValue) {
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
