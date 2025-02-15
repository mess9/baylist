package org.baylist.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.User;
import org.baylist.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.baylist.util.convert.ToJson.toJson;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class UserController {

	UserService userService;

	@GetMapping("/user")
	public String getUserInfo(@RequestParam String userId) {
		try {
			User userFromDb = userService.getUserFromDb(Long.parseLong(userId));
			return toJson(userFromDb);
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}

}
