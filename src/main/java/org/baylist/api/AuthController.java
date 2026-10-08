package org.baylist.api;

import lombok.RequiredArgsConstructor;
import org.baylist.security.JwtService;
import org.baylist.security.TelegramAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtService jwtService;
	private final TelegramAuthService telegramAuthService;

	@PostMapping("/telegram")
	public ResponseEntity<Map<String, String>> telegramAuth(@RequestParam Map<String, String> params) {
		// в подпись Telegram входят все поля виджета, кроме самого hash
		Map<String, String> fields = new HashMap<>(params);
		String hash = fields.remove("hash");

		if (!telegramAuthService.isValid(fields, hash)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String token = jwtService.generateToken(fields.get("id"));
		return ResponseEntity.ok(Map.of("token", token));
	}
}
