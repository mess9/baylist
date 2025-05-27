package org.baylist.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Dialog;
import org.baylist.db.entity.User;
import org.baylist.db.repo.UserRepository;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
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
import static org.baylist.dto.Constants.USER;
import static org.baylist.util.Util.getName;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

	UserRepository userRepository;
	ApplicationContext context;
	HistoryService historyService;

	@PersistenceContext
	EntityManager entityManager;

	@Getter
	Set<Long> modifiedUserIds = ConcurrentHashMap.newKeySet();

	Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();



	@CachePut(value = "user", key = "#user.userId")
	public User saveUserInCache(User user) {
		modifiedUserIds.add(user.getUserId());
		return user;
	}

	@Transactional
	public void saveUserInDb(User user) {
		try {
			entityManager.merge(user);
		} catch (DataAccessException e) {
			log.error("Ошибка при сохранении пользователя: {}", e.getMessage());
		}
	}

	@Transactional
	public void checkUser(ChatValue chatValue) {
		UserService self = context.getBean(UserService.class);
		if (chatValue.isCallback()) {
			Long userId = chatValue.getUpdate().getCallbackQuery().getFrom().getId();
			User user = self.findByUserId(userId);

			bindUser(chatValue, user); //кнопки можем показывать только уже известному юзеру, потому тут без проверки на наличие юзера
		} else {
			Long userId = chatValue.getUpdate().getMessage().getFrom().getId();
			User user = self.findByUserId(userId);

			if (user != null) {
				bindUser(chatValue, user);
			} else {
				createNewUser(chatValue, userId);
			}
		}
	}

	@Transactional
	@Cacheable(value = USER, unless = "#result == null")
	public User findByUserId(Long userId) {
		return userRepository.findByUserId(userId);
	}

	@Transactional
	@CacheEvict(value = USER, key = "#chatValue.getUser().userId")
	public boolean addFriend(ChatValue chatValue, Contact contact) {
		User user = getUserFromDb(chatValue.getUserId());
		User friend = getUserFromDb(contact.getUserId());

		if (friend != null) {
			if (existFriend(user, friend.getUserId())) {
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


	public User getUserFromDb(Long userId) { //no cache
		return userRepository.findByUserId(userId);
	}

	public User getUserFromWithFriendsDb(Long userId) { //no cache
		return userRepository.findUserWithMyFriends(userId);
	}

	private boolean existFriend(User user, Long friend) {
		if (user.getFriends().isEmpty()) {
			return false;
		} else {
			return user.getFriends().stream().anyMatch(f -> f.getUserId().equals(friend));
		}
	}

	public boolean isExistToken(Long userId) {
		return userRepository.findByUserId(userId).getTodoistToken() != null;
	}

	@Transactional
	protected boolean saveFriend(User user, User friend) {
		ReentrantLock lock = locks.computeIfAbsent(user.getUserId(), id -> new ReentrantLock());

		lock.lock();
		try {
			// Перепроверяем наличие друга под блокировкой, чтобы исключить
			// гонку, возникшую между вызовом existFriend и saveFriend
			if (existFriend(user, friend.getUserId())) {
				return false; // Друг уже добавлен, не делаем ничего
			}

			// Добавляем друга и сохраняем
			user.getFriends().add(friend);
			userRepository.save(user);
			return true;
		} finally {
			lock.unlock();
			// Опционально удаляем замок, если очередь пуста
			if (!lock.hasQueuedThreads()) {
				locks.remove(user.getUserId(), lock);
			}
		}

	}

	private User createNewFriend(Contact contact) {
		User newUser = new User();
		newUser.setUserId(contact.getUserId());
		newUser.setFirstName(contact.getFirstName());
		newUser.setLastName(contact.getLastName());
		newUser.setLastSeen(OffsetDateTime.now());
		newUser.setRegistered(OffsetDateTime.now());
		newUser.setDialog(new Dialog(newUser, contact.getUserId(), State.START));
		userRepository.save(newUser);
		log.info("created new friend - {}", newUser.getFirstName());
		return newUser;
	}

	public List<User> getFriendMe(Long userId) {
		return userRepository.findByFriendsUserId(userId);
	}

	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void saveToken(ChatValue chatValue, String token) {
		User user = chatValue.getUser();
		user.setTodoistToken(token);
		userRepository.save(user);
		chatValue.setState(State.START);
	}

	@Cacheable(value = "user", unless = "#result == null", key = STRING_FIL_USER_ID)
	public User getFil() {
		return userRepository.findByUserId(FIL_USER_ID);
	}

	@Transactional
	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void removeMyFriendsList(ChatValue chatValue) {
		User userFromDb = getUserFromDb(chatValue.getUserId());
		List<User> friends = userFromDb.getFriends();
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
					.callbackData(Callbacks.FRIEND_REMOVE_MY_CHOICE.getCallbackData() + ":" + f.getUserId())
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

	@Transactional
	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void removeFromFriendsList(ChatValue chatValue) {
		User userFromDb = getUserFromDb(chatValue.getUserId());
		List<User> friendMe = getFriendMe(userFromDb.getUserId());

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
					.callbackData(Callbacks.FRIEND_REMOVE_FROM_CHOICE.getCallbackData() + ":" + f.getUserId())
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
		Long friendUserId = Long.parseLong(chatValue.getCallbackData().substring(Callbacks.FRIEND_REMOVE_MY_CHOICE.getCallbackData().length() + 1));
		User friend = userRepository.findByUserId(friendUserId);
		User user = getUserFromDb(chatValue.getUserId());
		user.getFriends().remove(friend);
		userRepository.save(user);
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
		Long friendUserId = Long.parseLong(chatValue.getCallbackData().substring(Callbacks.FRIEND_REMOVE_FROM_CHOICE.getCallbackData().length() + 1));
		User friend = getUserFromDb(friendUserId);
		User user = getUserFromDb(chatValue.getUserId());
		friend.getFriends().remove(user);
		userRepository.save(friend);
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

	@Transactional
	public String removeMyFriend(Long userId, String friendName) {
		User user = userRepository.findUserWithMyFriends(userId);
		User friend = user.getFriends().stream()
				.filter(f -> friendName.contains(f.getFirstName()) ||
						friendName.contains(f.getLastName()) ||
						getName(f).contains(friendName))
				.findAny().orElse(null);
		if (friend != null) {
			user.getFriends().remove(friend);
			userRepository.save(user);
			historyService.changeFriend(user, friend, Action.REMOVE_MY_FRIEND);
			return friendName;
		} else {
			return "такой друг не был найден";
		}
	}

	@Transactional
	public String removeFromFriend(Long userId, String friendName) {
		User user = userRepository.findByUserId(userId);
		List<User> friendMe = getFriendMe(userId);
		User friend = friendMe.stream()
				.filter(f -> friendName.contains(f.getFirstName()) ||
						friendName.contains(f.getLastName()) ||
						getName(f).contains(friendName))
				.findAny().orElse(null);
		if (friend != null) {
			friend.getFriends().remove(user);
			userRepository.save(friend);
			historyService.changeFriend(user, friend, Action.REMOVE_FROM_FRIEND);
			return friendName;
		} else {
			return "такой друг не был найден";
		}
	}

	//private
	private void bindUser(ChatValue chatValue, User user) {
		chatValue.setUser(user);
		log.info("hi - {}, state - {}", user.getFirstName(), user.getDialog().getState());
	}

	private void createNewUser(ChatValue chatValue, Long userId) {
		User newUser = new User();
		newUser.setUserId(userId);
		newUser.setFirstName(chatValue.getUpdate().getMessage().getFrom().getFirstName());
		newUser.setLastName(chatValue.getUpdate().getMessage().getFrom().getLastName());
		newUser.setLastSeen(OffsetDateTime.now());
		newUser.setRegistered(OffsetDateTime.now());
		newUser.setDialog(new Dialog(newUser, chatValue.getChatId(), State.START));
		userRepository.save(newUser);
		chatValue.setUser(newUser);
		log.info("registered new user - {}", newUser.getFirstName());
		historyService.register(newUser, Action.REGISTERED);
	}

}

