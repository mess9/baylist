package org.baylist.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * Проверка данных Telegram Login Widget: https://core.telegram.org/widgets/login#checking-authorization
 */
@Service
public class TelegramAuthService {

	private static final Duration MAX_AUTH_AGE = Duration.ofDays(1);

	@Value("${telegram.bot.token}")
	private String botToken;

	/**
	 * @param fields все поля, пришедшие от виджета, кроме hash
	 */
	public boolean isValid(Map<String, String> fields, String hash) {
		if (hash == null || fields.get("id") == null || !isFresh(fields.get("auth_date"))) {
			return false;
		}
		try {
			StringBuilder dataCheck = new StringBuilder();
			new TreeMap<>(fields).forEach((k, v) -> {
				if (v != null) {
					if (!dataCheck.isEmpty()) dataCheck.append('\n');
					dataCheck.append(k).append('=').append(v);
				}
			});

			byte[] secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.getBytes(StandardCharsets.UTF_8));
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
			byte[] expected = mac.doFinal(dataCheck.toString().getBytes(StandardCharsets.UTF_8));

			byte[] actual = HexFormat.of().parseHex(hash);
			return MessageDigest.isEqual(expected, actual);
		} catch (GeneralSecurityException | IllegalArgumentException e) {
			return false;
		}
	}

	private boolean isFresh(String authDate) {
		try {
			Instant issued = Instant.ofEpochSecond(Long.parseLong(authDate));
			Instant now = Instant.now();
			return !issued.isAfter(now.plusSeconds(60)) && issued.isAfter(now.minus(MAX_AUTH_AGE));
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}
}
