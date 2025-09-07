package org.baylist.service;

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

	public void sendTasks(User sourceUser, User recipientUser, Action action, String content) {
		History event = History.builder()
				.source(sourceUser.userId())
				.recipient(recipientUser.userId())
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	public void changeFriend(User sourceUser, User friend, Action action) {
		String content = "User - " + sourceUser.userId() + " " + action + " - " + friend.userId();
		History event = History.builder()
				.source(sourceUser.userId())
				.recipient(sourceUser.userId())
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	public void register(User sourceUser, Action action) {
		String content = "User - " + sourceUser.userId() + " " + action;
		History event = History.builder()
				.source(sourceUser.userId())
				.recipient(sourceUser.userId())
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}

	public void changeDict(Long userId, Action action, String entity) {
		String content = "User - " + userId + " " + action + " - " + entity;
		History event = History.builder()
				.source(userId)
				.recipient(userId)
				.action(action)
				.date(OffsetDateTime.now())
				.content(content)
				.build();

		historyRepository.save(event);
	}
}

