package com.credo.credo_server.controller;

import com.credo.credo_server.common.GlobalExceptionHandler;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.dto.auth.UserDto;
import com.credo.credo_server.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@Test
	@DisplayName("returns success response for valid wechat login")
	void wechatLogin_success_returnsUnifiedResponse() throws Exception {
		PhoneLoginResponse response = new PhoneLoginResponse(
			"jwt-token",
			new UserDto(1L, null, null, null),
			true
		);
		when(authService.wechatLogin("login-code")).thenReturn(response);

		mockMvc.perform(post("/api/auth/mini/wechat-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"loginCode":"login-code"}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("SUCCESS"))
			.andExpect(jsonPath("$.data.token").value("jwt-token"))
			.andExpect(jsonPath("$.data.isNewUser").value(true))
			.andExpect(jsonPath("$.data.user.phone").isEmpty());
	}

	@Test
	@DisplayName("returns 400 when loginCode is missing")
	void wechatLogin_missingLoginCode_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/mini/wechat-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));

		verify(authService, never()).wechatLogin(anyString());
	}

	@Test
	@DisplayName("returns 400 when loginCode is blank")
	void wechatLogin_blankLoginCode_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/mini/wechat-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"loginCode":"  "}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));

		verify(authService, never()).wechatLogin(anyString());
	}
}
