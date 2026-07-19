package com.credo.credo_server.controller;

import com.credo.credo_server.common.ApiResponse;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.dto.auth.WeChatLoginRequest;
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

	@PostMapping("/wechat-login")
	public ApiResponse<PhoneLoginResponse> wechatLogin(@RequestBody WeChatLoginRequest request) {
		validateLoginCode(request);
		PhoneLoginResponse response = authService.wechatLogin(request.getLoginCode());
		return ApiResponse.success(response);
	}

	private static void validateLoginCode(WeChatLoginRequest request) {
		if (request == null || request.getLoginCode() == null || request.getLoginCode().isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER);
		}
	}
}
