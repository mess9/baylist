package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Dialog;
import org.baylist.db.entity.User;
import org.baylist.db.repo.UserRepository;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.baylist.dto.Constants.FIL_USER_ID;

@Component
@AllArgsConstructor
@Slf4j
public class UserService {

	UserRepository userRepository;

	@Transactional
	public void checkUser(ChatValue chatValue) {
		if (chatValue.isCallback()) {
			Long userId = chatValue.getUpdate().getCallbackQuery().getFrom().getId();
			Optional<User> user = userRepository.findByUserId(userId);

			getUserFromDb(chatValue, user);
		} else {
			Long userId = chatValue.getUpdate().getMessage().getFrom().getId();
			Optional<User> user = userRepository.findByUserId(userId);

			if (user.isPresent()) {
				getUserFromDb(chatValue, user);
			} else {
				createNewUser(chatValue, userId);
			}
		}
	}

	//private
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void getUserFromDb(ChatValue chatValue, Optional<User> user) {
		User existUser = user.get();
		chatValue.setUser(existUser);
		existUser.setLastSeen(OffsetDateTime.now());
		userRepository.save(existUser);
		log.info("hi - {}", existUser.getFirstName());
	}

	private void createNewUser(ChatValue chatValue, Long userId) {
		User newUser = new User();
		newUser.setUserId(userId);
		newUser.setFirstName(chatValue.getUpdate().getMessage().getFrom().getFirstName());
		newUser.setLastName(chatValue.getUpdate().getMessage().getFrom().getLastName());
		newUser.setLastSeen(OffsetDateTime.now());
		newUser.setDialog(new Dialog(newUser, chatValue.getChatId(), State.START));
		userRepository.save(newUser);
		chatValue.setUser(newUser);
		log.info("created new user - {}", newUser.getFirstName());
	}

	public User getFil() {
		return userRepository.findByUserId(FIL_USER_ID).orElseThrow();
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}






}
