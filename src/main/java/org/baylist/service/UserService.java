package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Dialog;
import org.baylist.db.entity.User;
import org.baylist.db.repo.UserRepository;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.baylist.dto.Constants.FIL_USER_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final ApplicationContext context;
	@Getter
	private final Set<Long> modifiedUserIds = ConcurrentHashMap.newKeySet();


	@CachePut(value = "user", key = "#user.userId")
	public User saveUserInCache(User user) {
		modifiedUserIds.add(user.getUserId());
		return user;
	}

	@Transactional
	public void saveUserInDb(User user) {
		userRepository.save(user);
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

	@Cacheable(value = "user", unless = "#result == null")
	public User findByUserId(Long userId) {
		return userRepository.findByUserId(userId);
	}

	@Transactional
	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public boolean addFriend(ChatValue chatValue, Contact contact) {
		User user = getUserFromDb(chatValue.getUser().getUserId());
		User friend = getUserFromDb(contact.getUserId());

		if (friend != null) {
			if (existFriend(user, friend.getUserId())) {
				return false;
			} else {
				user.getFriends().add(friend);
				userRepository.save(user);
				return true;
			}
		} else {
			return false;
		}
	}

	public User getUserFromDb(Long userId) { //no cache
		return userRepository.findByUserId(userId);
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

	public List<User> getFriendList(Long userId) {
		return userRepository.findByFriendsUserId(userId);
	}

	//private
	private void bindUser(ChatValue chatValue, User user) {
		chatValue.setUser(user);
		log.info("hi - {}, state - {}", user.getFirstName(), user.getDialog().getState());
	}

	@Cacheable(value = "user", key = "#result.userId")
	public User getFil() {
		return userRepository.findByUserId(FIL_USER_ID);
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
		log.info("created new user - {}", newUser.getFirstName());
	}

	@CacheEvict(value = "user", key = "#chatValue.getUser().userId")
	public void saveToken(ChatValue chatValue, String token) {
		User user = chatValue.getUser();
		user.setTodoistToken(token);
		userRepository.save(user);
		chatValue.setState(State.START);
	}
}

