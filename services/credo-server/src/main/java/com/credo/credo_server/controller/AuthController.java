package com.credo.credo_server.controller;

import com.credo.credo_server.common.ApiResponse;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.dto.auth.PhoneLoginRequest;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/mini")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/phone-login")
	public ApiResponse<PhoneLoginResponse> phoneLogin(@RequestBody PhoneLoginRequest request) {
		validateRequiredCodes(request);
		PhoneLoginResponse response = authService.phoneLogin(request.getLoginCode(), request.getPhoneCode());
		return ApiResponse.success(response);
	}

	private static void validateRequiredCodes(PhoneLoginRequest request) {
		if (request.getLoginCode() == null || request.getLoginCode().isBlank()
			|| request.getPhoneCode() == null || request.getPhoneCode().isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER);
		}
	}
}
