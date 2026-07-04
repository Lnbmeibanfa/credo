package com.credo.credo_server.support;

import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.service.JwtService;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenSupport {

	private final JwtService jwtService;

	public AuthTokenSupport(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	public Long requireUserId(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		String token = authorizationHeader.trim();
		if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
			token = token.substring(7).trim();
		}

		if (token.isEmpty()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		try {
			return jwtService.parseUserId(token);
		} catch (RuntimeException ex) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
	}
}
