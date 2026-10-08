package org.baylist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

	@Value("${jwt.secret:}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	public String generateToken(String telegramId) {
		return generateToken(new HashMap<>(), telegramId);
	}

	public String generateToken(Map<String, Object> extraClaims, String telegramId) {
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(telegramId)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSigningKey())
				.compact();
	}

	public String extractTelegramId(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token) {
		try {
			final Date expiration = extractClaim(token, Claims::getExpiration);
			return !expiration.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey signingKey;

	@PostConstruct
	void initSigningKey() {
		if (secretKey == null || secretKey.isBlank()) {
			// без JWT_SECRET ключ случайный: выданные токены живут до рестарта
			log.warn("JWT_SECRET не задан, JWT подписываются случайным ключом");
			signingKey = Jwts.SIG.HS256.key().build();
		} else {
			signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
		}
	}

	private SecretKey getSigningKey() {
		return signingKey;
	}
} 