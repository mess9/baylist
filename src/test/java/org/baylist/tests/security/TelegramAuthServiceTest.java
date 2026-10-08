package org.baylist.tests.security;

import org.baylist.security.TelegramAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

// без Spring и базы: проверяем только подпись Telegram Login Widget
class TelegramAuthServiceTest {

	private static final String BOT_TOKEN = "123456:test-bot-token";

	private final TelegramAuthService service = service(BOT_TOKEN);

	@Test
	void validSignatureAccepted() throws Exception {
		Map<String, String> fields = fields(Instant.now());
		assertThat(service.isValid(fields, sign(fields, BOT_TOKEN))).isTrue();
	}

	@Test
	void lastNameIsPartOfSignature() throws Exception {
		Map<String, String> fields = fields(Instant.now());
		fields.put("last_name", "Иванов");
		assertThat(service.isValid(fields, sign(fields, BOT_TOKEN))).isTrue();
	}

	@Test
	void substitutedIdRejected() throws Exception {
		Map<String, String> fields = fields(Instant.now());
		String hash = sign(fields, BOT_TOKEN);
		fields.put("id", "42");
		assertThat(service.isValid(fields, hash)).isFalse();
	}

	@Test
	void signatureFromAnotherBotRejected() throws Exception {
		Map<String, String> fields = fields(Instant.now());
		assertThat(service.isValid(fields, sign(fields, "999:other-bot"))).isFalse();
	}

	@Test
	void staleAuthDateRejected() throws Exception {
		Map<String, String> fields = fields(Instant.now().minusSeconds(2 * 24 * 3600));
		assertThat(service.isValid(fields, sign(fields, BOT_TOKEN))).isFalse();
	}

	@Test
	void garbageHashRejected() {
		Map<String, String> fields = fields(Instant.now());
		assertThat(service.isValid(fields, null)).isFalse();
		assertThat(service.isValid(fields, "")).isFalse();
		assertThat(service.isValid(fields, "not-hex")).isFalse();
	}

	private static TelegramAuthService service(String botToken) {
		TelegramAuthService service = new TelegramAuthService();
		ReflectionTestUtils.setField(service, "botToken", botToken);
		return service;
	}

	private static Map<String, String> fields(Instant authDate) {
		Map<String, String> fields = new HashMap<>();
		fields.put("id", "100500");
		fields.put("first_name", "Тест");
		fields.put("username", "tester");
		fields.put("auth_date", String.valueOf(authDate.getEpochSecond()));
		return fields;
	}

	private static String sign(Map<String, String> fields, String botToken) throws Exception {
		StringBuilder dataCheck = new StringBuilder();
		new TreeMap<>(fields).forEach((k, v) -> dataCheck.append(dataCheck.isEmpty() ? "" : "\n").append(k).append('=').append(v));
		byte[] secret = MessageDigest.getInstance("SHA-256").digest(botToken.getBytes(StandardCharsets.UTF_8));
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret, "HmacSHA256"));
		return HexFormat.of().formatHex(mac.doFinal(dataCheck.toString().getBytes(StandardCharsets.UTF_8)));
	}
}
