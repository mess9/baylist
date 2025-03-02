package org.baylist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${jwt.secret}")
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

	private SecretKey getSigningKey() {
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
} 