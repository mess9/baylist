package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.History;
import org.baylist.db.entity.User;
import org.baylist.db.repo.HistoryRepository;
import org.baylist.dto.telegram.Action;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class HistoryService {

	HistoryRepository historyRepository;

	@Transactional
	public void sendTasks(User sourceUser, User recipientUser, Action action, String content) {
		History event = History.builder()
				.source(sourceUser)
				.recipient(recipientUser)
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	@Transactional
	public void changeFriend(User sourceUser, User friend, Action action) {
		String content = "User - " + sourceUser.getUserId() + " " + action + " - " + friend.getUserId();
		History event = History.builder()
				.source(sourceUser)
				.recipient(sourceUser)
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	@Transactional
	public void register(User sourceUser, Action action) {
		String content = "User - " + sourceUser.getUserId() + " " + action;
		History event = History.builder()
				.source(sourceUser)
				.recipient(sourceUser)
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	@Transactional
	public void changeDict(Long userId, Action action, String categoryName) {
		String content = "User - " + userId + " " + action + " - " + categoryName;
		User user = new User();
		user.setUserId(userId);
		History event = History.builder()
				.source(user)
				.recipient(user)
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}
}

