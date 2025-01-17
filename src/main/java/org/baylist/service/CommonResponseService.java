package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.User;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.Commands;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.baylist.util.Util.getName;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommonResponseService {

	TodoistService todoist;
	UserService userService;
	DictionaryService dictionaryService;
	MenuService menuService;


	public void cancelMessage(ChatValue chatValue) {
		chatValue.setReplyText("ок. в следующий раз будут деяния\n" +
				"а пока в режиме ожидания списка задач.");
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

	public void checkAndView(ChatValue chatValue, boolean isMenu) {
		List<User> recipients = todoist.checkRecipients(chatValue);

		if (recipients.isEmpty()) {
			menuService.mainMenu(chatValue, false);
			chatValue.setReplyText("""
					нет возможности посмотреть то чего не существует.
					<i>но может быть однажды...</i>
					""");
			chatValue.setReplyParseModeHtml();
		} else if (recipients.size() == 1) {
			view(chatValue, recipients.getFirst(), isMenu);
		} else {
			chatValue.setReplyText("выберите чьи задачки посмотреть");
			chatValue.setReplyKeyboard(recipientsKeyboard(recipients, false));
		}
	}

	public void view(ChatValue chatValue, User recipient, boolean isMenu) {
		if (isMenu) {
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
					new InlineKeyboardRow(InlineKeyboardButton.builder()
							.text("назад")
							.callbackData(Callbacks.MAIN_MENU.getCallbackData())
							.build())));
			chatValue.setEditText(todoist.getBuylistProject(recipient));
			chatValue.setEditReplyParseModeHtml();
			chatValue.setEditReplyKeyboard(markup);
			chatValue.setState(State.MENU);
		} else {
			chatValue.setReplyText(todoist.getBuylistProject(recipient));
			chatValue.setReplyParseModeHtml();
			chatValue.setState(State.DEFAULT);
		}
	}

	public InlineKeyboardMarkup recipientsKeyboard(List<User> recipients, boolean isSend) {
		List<InlineKeyboardRow> rows = new LinkedList<>();
		Callbacks callbacks;
		if (isSend) {
			callbacks = Callbacks.SEND_TASK_TO;
		} else {
			callbacks = Callbacks.VIEW_TASK_TO;
		}
		recipients.forEach(r -> rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
				.text(getName(r))
				.callbackData(callbacks.getCallbackData() + r.getUserId())
				.build())));
		rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
				.text("отмена")
				.callbackData(Callbacks.CANCEL.getCallbackData())
				.build()));
		return new InlineKeyboardMarkup(rows);
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
		List<User> friendList = userService.getFriendMe(user.getUserId());


		sb.append("<b>сводная информация:</b>\n")
				.append("вас зовут - ")
				.append(getName(user))
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
			checkFriends(friends, sb);
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
				checkFriendsMe(friendList, sb);
			}
		}

		chatValue.setEditText(sb.toString());
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
		chatValue.setState(State.MENU);
	}

	private void checkFriends(List<User> friends, StringBuilder sb) {
		if (!friends.isEmpty()) {
			if (friends.size() > 1) {
				sb.append("у вас есть друзья:\n");
				friends.forEach(f -> sb.append(" <code>")
						.append(getName(f))
						.append("</code>\n"));
			} else {
				sb.append("у вас есть друг!\n");
				friends.forEach(f -> sb.append(" <code>")
						.append(getName(f))
						.append("</code>\n"));
			}
		} else {
			sb.append("у вас нет друзей\n");
		}
	}

	private void checkFriendsMe(List<User> friendList, StringBuilder sb) {
		if (friendList.size() > 1) {
			sb.append("друзья, которым вы можете отправлять задачи:\n");
			friendList.forEach(f -> sb.append(" <code>")
					.append(getName(f))
					.append("</code>\n"));
		} else {
			sb.append("друг, которому вы можете отправлять задачи:\n");
			friendList.forEach(f -> sb.append(" <code>")
					.append(getName(f))
					.append("</code>\n"));
		}
	}

	public void cancel(ChatValue chatValue, boolean isEdit) {
		if (isEdit) {
			chatValue.setEditText("вернулся в режим приёма задач");
		} else {
			chatValue.setReplyText("вернулся в режим приёма задач");
		}
		chatValue.setState(State.DEFAULT);
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
				🔑 подключение todoist
				<b> как получить токен </b>
				0. регистрируемся на todoist.com
				1. переходим по ссылке https://todoist.com/prefs/integrations
				(если в приложении - настройки -> интеграции -> для разработчиков)
				2. переключаемся на вкладку "для разработчиков"
				3. копируем токен и отправляем его боту (вставить в чат, в ответ на это сообщение)
				
				👉 в будущем todoist станет опциональным, но пока <i>и так сойдёт</i> 😊.
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
			todoist.createProject(chatValue);
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
					🔑 токен получен, спасибо!
					
					в <b>todoist</b> будет создан проект <b>"buylist"</b>, куда будут отправляться задачи от вас и ваших друзей
					<i>(если они у вас есть 😏)</i>.
					
					даже если лимит проектов в todoist исчерпан, бот всё равно его создаст – это такая api-магия ✨
					""");
			chatValue.setReplyKeyboard(markup);
			chatValue.setReplyParseModeHtml();
		} else {
			chatValue.setReplyText("""
					неверный токен, попробуйте ещё раз
					
					или воспользуйтесь командой /report дабы описать что именно пошло не так.
					""");
		}
	}

	@Transactional
	public void listMyFriends(ChatValue chatValue) {
		User user = userService.getUserFromDb(chatValue.getUser().getUserId());
		List<User> friends = user.getFriends();
		StringBuilder sb = new StringBuilder();
		checkFriends(friends, sb);
		chatValue.setEditText(sb.toString());
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("назад")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build())));
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
	}

	@Transactional
	public void listFriendsMe(ChatValue chatValue) {
		List<User> friendsMe = userService.getFriendMe(chatValue.getUser().getUserId());
		StringBuilder sb = new StringBuilder();
		if (friendsMe.isEmpty()) {
			sb.append("у вас пока нет друзей которым вы бы могли отправить задачи...");
		} else {
			checkFriendsMe(friendsMe, sb);
		}
		chatValue.setEditText(sb.toString());
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("назад")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build())));
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
	}

	public void start(ChatValue chatValue) {
		chatValue.setReplyText("""
				🎉 YAY
				бот создан филом, чтобы удобно отправлять списки покупок и дел.
				
				🛠 основная идея
				бот помогает автоматически добавлять задачи в <b>todoist</b> прямо из telegram.
				
				раньше я просто писал списки в «избранное» в telegram, <b>но:</b>
				❌ нельзя отмечать выполненные задачи
				❌ приходится разбивать задачи по категориям вручную
				
				✔️ а тут бот принимает списки задач, сам разбивает их на категории и отправляет в todoist 📌.
				
				📂 гибкая настройка категорий
				<s>нужно</s>можно создать свои категории и варианты задач для них
				
				👥 совместное использование
				можете использовать бота вместе с друзьями и семьёй:
				
				отправлять задачи себе 📝
				получать задачи от других 🏷
				и так и так одновременно 🔄
				
				🔑 подключение todoist
				чтобы бот мог отправлять вам задачи, нужно:
				зарегистрироваться в <b>todoist</b>
				получить api-токен (инструкция будет позже 📌)
				👉 в будущем todoist станет опциональным, но пока <i>и так сойдёт</i> 😊.
				
				⚙ первоначальная настройка
				теперь давайте настроим бота! 🚀
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

	public void dictHelp(ChatValue chatValue) {
		chatValue.setEditText("""
				📖 как работает словарик
				
				<i><b>категории</b> и <b>варианты</b> – это мой <u>внутренний словарик</u>. он помогает автоматически распределять задачи по категориям в todoist.</i>
				
				🔹 <b>категории</b> в todoist появятся только после первой задачи, отправленной в них.
				🔹 чтобы я правильно раскидывал задачи, мне нужно заранее знать о них – для этого мы и заполняем словарик.
				
				📌 пример:
				в словарике есть две категории:
				
				🛒 <i>продукты</i>: <code>помидоры, картошка, морковка</code>
				📦 <i>пункты выдачи</i>: <code>вб, озон, почта</code>
				если ты отправишь мне:
				<code>морковка</code>
				<code>вб</code>
				
				то в todoist появятся две категории, и в каждой будет по одной задаче ✅.
				
				если задача не матчится ни в одну из категорий(или если их нет) - она будет добавлена в список задач вне всяких категорий
				""");
		chatValue.setEditReplyParseModeHtml();
		chatValue.setState(State.DICT_SETTING);
		chatValue.setEditReplyKeyboard(new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(
				InlineKeyboardButton.builder()
						.text("ok")
						.callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
						.build()))));
	}

	public void doneWithouFriends(ChatValue chatValue, State state) {
		chatValue.setReplyText("""
				<i> если бы у вас был токен todoist вы бы могли добавить друзей которые могут отправлять вам задачи </i>
				
				но у вас его нет
				так что бот для вас будет бесполезен до тех пор, пока ваш друг не добавит вас в к себе в профиль, что бы вы могли отправлять ему задачи
				
				настройка завершена.
				можно зайти в меню и заново настроить токен
				/menu
				или снова в режим первоначальной настройки
				/start
				""");
		chatValue.setReplyParseModeHtml();
		chatValue.setState(state);
	}

	public void addFriend(ChatValue chatValue, Update update) {
		Contact contact = update.getMessage().getContact();
		if (userService.addFriend(chatValue, contact)) {
			chatValue.setReplyText("друг добавлен");
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
					List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
									.text("добавить ещё")
									.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
									.build()),
							new InlineKeyboardRow(InlineKeyboardButton.builder()
									.text("друзья кончились")
									.callbackData(Callbacks.START_DONE.getCallbackData())
									.build())
					));
			chatValue.setReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("такой друг у тебя уже есть, давай другого");
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
					List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
									.text("добавить ещё")
									.callbackData(Callbacks.START_2_ADD_FRIENDS.getCallbackData())
									.build()),
							new InlineKeyboardRow(InlineKeyboardButton.builder()
									.text("друзья закончились")
									.callbackData(Callbacks.START_DONE.getCallbackData())
									.build())
					));
			chatValue.setReplyKeyboard(markup);
		}
	}

	public void friendsHelp(ChatValue chatValue) {
		chatValue.setEditText("""
				<u>основная идея бота – отправлять себе задачи в todoist</u> и позволять друзьям/семье делать то же самое, отправляя задачи <u>в твой</u> todoist.
				
				из этого следует:
				1️⃣ если у тебя привязан todoist, ты можешь:
				отправлять себе задачи 📌
				получать задачи от друзей, если добавил их в бота 👥
				
				2️⃣ если у тебя <b>нет</b> привязанного todoist, ты можешь:
				отправлять задачи только тем, у кого он есть ✅
				только если этот человек добавил тебя в друзья 🔑
				
				3️⃣ если у тебя есть todoist и тебя добавили в друзья другие пользователи с todoist, то при отправке задач появится выбор:
				отправить себе 📌
				отправить одному из друзей 👥
				<i>разбиение задач по категориям работает по настройкам владельца todoist. как он настроил категории – так и будут распределяться входящие задачи, от него самого или от друзей.</i>
				
				🔹 <b>важно:</b> чтобы друг мог отправлять тебе задачи, у тебя должен быть todoist и ты должен добавить его в друзья в настройках бота.
				""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("назад")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build())));
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
	}

	public void friendsRequest(ChatValue chatValue, State state) {
		chatValue.setReplyText("""
				<b> добавление друзей! </b>
				
				Как поделиться контактом
				— открываем чат с нужным человеком
				— нажмите значок трех точек. откроется окошко меню. выберите пункт «Поделиться контактом».
				— откроется окно — со списком контактов. выберете бот buylist (имя для поиска - buylistFAbot)
				""");
		chatValue.setReplyParseModeHtml();
		chatValue.setState(state);
	}


}
