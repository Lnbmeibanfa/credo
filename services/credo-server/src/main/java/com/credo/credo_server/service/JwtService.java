package com.credo.credo_server.service;

import com.credo.credo_server.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

	private final JwtProperties jwtProperties;
	private final SecretKey secretKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(Long userId, String phone) {
		Instant now = Instant.now();
		Instant expiry = now.plus(jwtProperties.getExpireHours(), ChronoUnit.HOURS);

		var builder = Jwts.builder()
			.subject(String.valueOf(userId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.signWith(secretKey);

		if (phone != null && !phone.isBlank()) {
			builder.claim("phone", phone);
		}

		return builder.compact();
	}

	public Long parseUserId(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return Long.parseLong(claims.getSubject());
	}
}
