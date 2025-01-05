package org.baylist.config;

import lombok.AllArgsConstructor;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class Scheduler {

	private final CacheManager cacheManager;
	private final UserService userService;


	@Transactional
	@Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
	public void syncUserCacheToDatabase() {
		var cache = cacheManager.getCache("user");
		Set<Long> modifiedUserIds = userService.getModifiedUserIds();
		if (cache != null) {
			for (Long userId : modifiedUserIds) {
				User user = cache.get(userId, User.class);
				if (user != null) {
					userService.saveUserInDb(user);
				}
			}

			modifiedUserIds.clear();
			cache.clear();
		}
	}


}
