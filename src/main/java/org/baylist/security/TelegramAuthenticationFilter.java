package org.baylist.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class TelegramAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Value("${telegram.bot.token}")
	private String botToken;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		if (!request.getRequestURI().startsWith("/api/auth/telegram")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Получаем параметры от Telegram Login Widget
		String authDate = request.getParameter("auth_date");
		String firstName = request.getParameter("first_name");
		String id = request.getParameter("id");
		String username = request.getParameter("username");
		String photoUrl = request.getParameter("photo_url");
		String hash = request.getParameter("hash");

		if (validateTelegramAuth(authDate, firstName, id, username, photoUrl, hash)) {
			// Создаем JWT токен
			String token = jwtService.generateToken(id);
			response.setHeader("Authorization", "Bearer " + token);

			// Устанавливаем аутентификацию
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					id,
					null,
					null
			);
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		filterChain.doFilter(request, response);
	}

	private boolean validateTelegramAuth(String authDate, String firstName, String id, String username, String photoUrl, String hash) {
		try {
			TreeMap<String, String> params = new TreeMap<>();
			if (authDate != null) params.put("auth_date", authDate);
			if (firstName != null) params.put("first_name", firstName);
			if (id != null) params.put("id", id);
			if (username != null) params.put("username", username);
			if (photoUrl != null) params.put("photo_url", photoUrl);

			StringBuilder dataCheck = new StringBuilder();
			params.forEach((k, v) -> dataCheck.append(k).append("=").append(v).append("\n"));
			dataCheck.setLength(dataCheck.length() - 1); // Удаляем последний \n

			SecretKeySpec secretKeySpec = new SecretKeySpec(
					sha256(botToken.getBytes(StandardCharsets.UTF_8)),
					"HmacSHA256"
			);
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKeySpec);

			String generatedHash = bytesToHex(mac.doFinal(dataCheck.toString().getBytes()));
			return generatedHash.equals(hash);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			return false;
		}
	}

	private byte[] sha256(byte[] input) throws NoSuchAlgorithmException {
		return java.security.MessageDigest.getInstance("SHA-256").digest(input);
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}
} 