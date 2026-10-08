package org.baylist.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class UserController {

	UserService userService;

	// только свой профиль: id берётся из JWT, токен Todoist наружу не отдаётся
	@GetMapping("/user")
	public ResponseEntity<UserInfo> getUserInfo(Authentication authentication) {
		User user = userService.getUserFromDb(Long.parseLong(authentication.getName()));
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(new UserInfo(
				user.userId(),
				user.firstName(),
				user.lastName(),
				user.todoistToken() != null,
				user.registered(),
				user.lastSeen()));
	}

	public record UserInfo(Long userId,
	                       String firstName,
	                       String lastName,
	                       boolean todoistConnected,
	                       OffsetDateTime registered,
	                       OffsetDateTime lastSeen) {
	}

}
