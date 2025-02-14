package org.baylist.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.baylist.dto.Constants.USER;

@Component
@AllArgsConstructor
@Slf4j
public class Scheduler {

	private final CacheManager cacheManager;
	private final UserService userService;

	/**
	 * Sync user cache to database
	 */
	@Transactional
	@Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
	public void syncUserCacheToDatabase() {
		var cache = cacheManager.getCache(USER);
		Set<Long> modifiedUserIds = userService.getModifiedUserIds();

		if (cache != null) {
			Set<Long> failedUserIds = new HashSet<>();

			for (Long userId : modifiedUserIds) {
				User user = cache.get(userId, User.class);
				if (user != null) {
					try {
						userService.saveUserInDb(user);
					} catch (Exception e) {
						log.error("Ошибка при сохранении пользователя {}: {}", userId, e.getMessage());
						failedUserIds.add(userId);
					}
				}
			}

			// Очищаем кеш только если ВСЕ пользователи были успешно сохранены
			if (failedUserIds.isEmpty()) {
				modifiedUserIds.clear();
				cache.clear();
			} else {
				log.warn("Не удалось сохранить пользователей: {}. Оставляем их в кеше для повторной попытки.", failedUserIds);
			}
		}
	}


}
