package org.baylist.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Dialog;
import org.baylist.db.entity.User;
import org.baylist.db.repo.FriendsDao;
import org.baylist.db.repo.UserRepository;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.baylist.dto.Constants.FIL_USER_ID;
import static org.baylist.dto.Constants.STRING_FIL_USER_ID;
import static org.baylist.util.Util.getName;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

	UserRepository userRepository;
	DialogService dialogService;
	FriendsDao friendsDao;

	HistoryService historyService;

	@Getter
	Set<Long> modifiedUserIds = ConcurrentHashMap.newKeySet();

	Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

	/* ===================== cache utils ===================== */

	public void saveUserInDb(User user) {
		try {
			userRepository.save(user);
		} catch (DataAccessException e) {
			log.error("Ошибка при сохранении пользователя: {}", e.getMessage());
		}
	}

	public User getUserFromDb(long userId) { //no cache
		return userRepository.findByUserId(userId);
	}

	/* ===================== bootstrap / binding ===================== */

	@Transactional
	public void checkUser(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			Long userId = chatValue.getUpdate().getCallbackQuery().getFrom().getId();
			User user = getUserFromDb(userId);
			bindUser(chatValue, user); // кнопки можем показывать только уже известному юзеру
		} else {
			Long userId = chatValue.getUpdate().getMessage().getFrom().getId();
			User user = getUserFromDb(userId);
			if (user != null) {
				bindUser(chatValue, user);
			} else {
				createNewUser(chatValue, userId);
				chatValue.setState(State.START);
			}
		}
	}

	/* ===================== friends ===================== */


	public boolean addFriend(ChatValue chatValue, Contact contact) {
		User user = getUserFromDb(chatValue.getUserId());
		User friend = getUserFromDb(contact.getUserId());

		if (friend != null) {
			if (existFriend(user, friend.userId())) {
				return false;
			} else {
				historyService.changeFriend(chatValue.getUser(), friend, Action.ADD_FRIEND);
				return saveFriend(user, friend);
			}
		} else {
			User newFriend = createNewFriend(contact);
			return saveFriend(user, newFriend);
		}
	}

	public List<User> getFriendMe(long userId) {
		return userRepository.friendMe(userId);
	}

	public List<User> getFriends(long userId) {
		return userRepository.friendsOf(userId);
	}

	private boolean existFriend(Long userId, Long friendId) {
		return friendsDao.existsLink(userId, friendId);
	}

	@Transactional
	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void removeMyFriendsList(ChatValue chatValue) {
		long me = chatValue.getUserId();
		List<User> friends = getFriends(me);

		InlineKeyboardMarkup markup;
		if (friends.isEmpty()) {
			chatValue.setEditText("если у вас нет <s>собаки</s>друга,\nего вам и не удалить");
			chatValue.setEditReplyParseModeHtml();
			markup = new InlineKeyboardMarkup(
					List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
							.text("ээх")
							.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
							.build())
					));
		} else {
			List<InlineKeyboardRow> rows = new LinkedList<>();
			friends.forEach(f -> rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
					.callbackData(Callbacks.FRIEND_REMOVE_MY_CHOICE.getCallbackData() + ":" + f.userId())
					.text(getName(f))
					.build())));
			rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
					.text("назад")
					.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
					.build()));
			markup = new InlineKeyboardMarkup(rows);
			chatValue.setEditText("выберете друга, который больше не сможет отправлять вам задачи");
		}
		chatValue.setEditReplyKeyboard(markup);
	}

	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void removeFromFriendsList(ChatValue chatValue) {
		User me = getUserFromDb(chatValue.getUserId());
		List<User> friendMe = getFriendMe(me.userId());

		InlineKeyboardMarkup markup;
		if (friendMe.isEmpty()) {
			chatValue.setEditText("тебя никто не добавлял себе в друзья");
			chatValue.setEditReplyParseModeHtml();
			markup = new InlineKeyboardMarkup(
					List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
							.text("ээх")
							.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
							.build())
					));
		} else {
			List<InlineKeyboardRow> rows = new LinkedList<>();
			friendMe.forEach(f -> rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
					.callbackData(Callbacks.FRIEND_REMOVE_FROM_CHOICE.getCallbackData() + ":" + f.userId())
					.text(getName(f))
					.build())));
			rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
					.text("назад")
					.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
					.build()));
			markup = new InlineKeyboardMarkup(rows);
			chatValue.setEditText("выберете друга, которому вы не хотите отправлять задачи");
		}
		chatValue.setEditReplyKeyboard(markup);
	}

	@Transactional
	public void removeMyFriend(ChatValue chatValue) {
		long friendUserId = Long.parseLong(
				chatValue.getCallbackData()
						.substring(Callbacks.FRIEND_REMOVE_MY_CHOICE.getCallbackData().length() + 1)
		);
		User friend = getUserFromDb(friendUserId);
		User me = getUserFromDb(chatValue.getUserId());

		friendsDao.remove(me.userId(), friend.userId()); // удаляем линк (me -> friend)
		historyService.changeFriend(chatValue.getUser(), friend, Action.REMOVE_MY_FRIEND);

		chatValue.setEditText("вы удалили друга\nживите дальше в проклятом мире, который сами и создали");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("ээх")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build())
				));
		chatValue.setEditReplyKeyboard(markup);
	}

	@Transactional
	public void removeFromFriend(ChatValue chatValue) {
		long friendUserId = Long.parseLong(
				chatValue.getCallbackData()
						.substring(Callbacks.FRIEND_REMOVE_FROM_CHOICE.getCallbackData().length() + 1)
		);
		User friend = getUserFromDb(friendUserId);
		User me = getUserFromDb(chatValue.getUserId());

		friendsDao.remove(friend.userId(), me.userId()); // удаляем линк (friend -> me)
		historyService.changeFriend(chatValue.getUser(), friend, Action.REMOVE_FROM_FRIEND);

		chatValue.setEditText("""
				друг тебе доверился, открылся
				а ты его, вычеркнул
				<i>живи теперь с этим</i>""");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
				List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
						.text("ээх")
						.callbackData(Callbacks.FRIENDS_SETTINGS.getCallbackData())
						.build())
				));
		chatValue.setEditReplyKeyboard(markup);
		chatValue.setEditReplyParseModeHtml();
	}

	/* ===================== helpers ===================== */

	private void bindUser(ChatValue chatValue, User user) {
		chatValue.setUser(user);
		chatValue.setState(dialogService.getState(user.userId()));
		log.info("hi - {}, state - {}", user.firstName(), dialogService.getDialog(user.userId()).state());
	}

	@Transactional
	public void createNewUser(ChatValue chatValue, Long userId) {
		String fn = chatValue.getUpdate().getMessage().getFrom().getFirstName();
		String ln = chatValue.getUpdate().getMessage().getFrom().getLastName();

		OffsetDateTime now = OffsetDateTime.now();
		User newUser = new User(userId, fn, ln, null, now, now);
		userRepository.save(newUser);

		// создаём диалог (user_id UNIQUE): chatId = текущий чат
		dialogService.saveDialog(new Dialog(
				null,
				newUser.userId(),
				chatValue.getChatId(),
				State.START
		));

		chatValue.setUser(newUser);
		log.info("registered new user - {}", newUser.firstName());
		historyService.register(newUser, Action.REGISTERED);
	}

	private boolean existFriend(User user, Long friend) {
		List<User> friends = userRepository.friendMe(user.userId());
		if (friends.isEmpty()) {
			return false;
		} else {
			return friends.stream().anyMatch(f -> f.userId().equals(friend));
		}
	}

	public boolean isExistToken(Long userId) {
		return userRepository.findByUserId(userId).todoistToken() != null;
	}

	private boolean saveFriend(User user, User friend) {
		// оставлено для совместимости вызовов — теперь вся логика в friendsDao
		ReentrantLock lock = locks.computeIfAbsent(user.userId(), id -> new ReentrantLock());
		lock.lock();
		try {
			if (existFriend(user.userId(), friend.userId())) return false;
			return friendsDao.add(user.userId(), friend.userId()) > 0;
		} finally {
			lock.unlock();
			if (!lock.hasQueuedThreads()) {
				locks.remove(user.userId(), lock);
			}
		}
	}

	private User createNewFriend(Contact contact) {
		User newUser = new User(
				contact.getUserId(),
				contact.getFirstName(),
				contact.getLastName(),
				null,
				OffsetDateTime.now(),
				OffsetDateTime.now()
		);
		Dialog dialog = new Dialog(
				null,
				contact.getUserId(),
				contact.getUserId(),
				State.START
		);
		dialogService.saveDialog(dialog);
		userRepository.save(newUser);
		userRepository.save(newUser);
		log.info("created new friend - {}", newUser.firstName());
		return newUser;
	}


	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	@Transactional
	public void saveToken(ChatValue chatValue, String token) {
		User u = getUserFromDb(chatValue.getUserId());
		User updated = new User(
				u.userId(),
				u.firstName(),
				u.lastName(),
				token,
				u.registered(),
				u.lastSeen()
		);
		userRepository.save(updated);
		chatValue.setState(State.START);
	}

	@Cacheable(value = "user", unless = "#result == null", key = STRING_FIL_USER_ID)
	public User getFil() {
		return userRepository.findByUserId(FIL_USER_ID);
	}


}

