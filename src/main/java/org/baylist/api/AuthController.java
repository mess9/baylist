package org.baylist.api;

import lombok.RequiredArgsConstructor;
import org.baylist.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtService jwtService;

	@PostMapping("/telegram")
	public ResponseEntity<Map<String, String>> telegramAuth(
			@RequestParam String id,
			@RequestParam String first_name,
			@RequestParam(required = false) String username,
			@RequestParam String auth_date,
			@RequestParam String hash
	) {
		String token = jwtService.generateToken(id);
		return ResponseEntity.ok(Map.of("token", token));
	}
} 