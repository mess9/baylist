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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
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

	//private
	private void bindUser(ChatValue chatValue, User user) {
		chatValue.setUser(user);
		log.info("hi - {}, state - {}", user.getFirstName(), user.getDialog().getState());
	}

	public void saveToken(ChatValue chatValue, String token) {
		User user = chatValue.getUser();
		user.setTodoistToken(token);
		userRepository.save(user);
		chatValue.setState(State.START);
	}

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


}
