package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Dialog;
import org.baylist.db.entity.User;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.UserRepository;
import org.baylist.dto.telegram.ChatState;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.baylist.dto.Constants.FIL_USER_ID;

@Component
@AllArgsConstructor
@Slf4j
public class UserService {

	UserRepository userRepository;
	CategoryRepository categoryRepository;

	@Transactional
	public void checkUser(ChatState chatState) {
		if (chatState.getUpdate().hasMessage()) {
			if (chatState.getUpdate().getMessage().isUserMessage()) {
				Long userId = chatState.getUpdate().getMessage().getFrom().getId();

				Optional<User> user = userRepository.findByUserId(userId);

				if (user.isPresent()) {
					User existUser = user.get();
					chatState.setUser(existUser);
					existUser.setLastSeen(OffsetDateTime.now());
					userRepository.save(existUser);
					log.info("hi - {}", existUser.getFirstName());
				} else {
					User newUser = new User();
					newUser.setUserId(userId);
					newUser.setFirstName(chatState.getUpdate().getMessage().getFrom().getFirstName());
					newUser.setLastName(chatState.getUpdate().getMessage().getFrom().getLastName());
					newUser.setLastSeen(OffsetDateTime.now());
					newUser.setDialog(new Dialog(newUser, chatState.getChatId()));
					userRepository.save(newUser);
					chatState.setUser(newUser);
					log.info("created new user - {}", newUser.getFirstName());
				}
			}
		} else if (chatState.getUpdate().hasCallbackQuery()) {
			Long userId = chatState.getUpdate().getCallbackQuery().getFrom().getId();
			Optional<User> user = userRepository.findByUserId(userId);
			chatState.setUser(user.orElseThrow());
		}
	}

	public User getFil() {
		return userRepository.findByUserId(FIL_USER_ID).orElseThrow();
	}

	public void feedbackOff(ChatState chatState) {
		User user = chatState.getUser();
		user.getDialog().setReport(false);
		userRepository.save(user);
	}

	public void feedbackOn(ChatState chatState) {
		User user = chatState.getUser();
		user.getDialog().setReport(true);
		userRepository.save(user);
	}

	public void addCategoryOff(ChatState chatState) {
		User user = chatState.getUser();
		user.getDialog().setAddCategory(false);
		userRepository.save(user);
	}

	public void addCategoryOn(ChatState chatState) {
		User user = chatState.getUser();
		user.getDialog().setAddCategory(true);
		userRepository.save(user);
	}

	public void addCategoryContext(User user, String category) {
		Category categoryDb = categoryRepository.findCategoryByName(category);
		user.getDialog().setCategoryAddedValues(categoryDb.getId());
		userRepository.save(user);
		//todo тут я остноавился
	}




}
