package com.credo.credo_server.controller;

import com.credo.credo_server.common.GlobalExceptionHandler;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.dto.auth.UserDto;
import com.credo.credo_server.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AuthService authService;

	@Test
	@DisplayName("returns success response for valid phone login")
	void phoneLogin_success_returnsUnifiedResponse() throws Exception {
		PhoneLoginResponse response = new PhoneLoginResponse(
			"jwt-token",
			new UserDto(1L, "13800138000", null, null),
			true
		);
		when(authService.phoneLogin("login-code", "phone-code")).thenReturn(response);

		mockMvc.perform(post("/api/auth/mini/phone-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"loginCode":"login-code","phoneCode":"phone-code"}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("SUCCESS"))
			.andExpect(jsonPath("$.data.token").value("jwt-token"))
			.andExpect(jsonPath("$.data.isNewUser").value(true))
			.andExpect(jsonPath("$.data.user.phone").value("13800138000"));
	}

	@Test
	@DisplayName("returns 400 when loginCode is missing")
	void phoneLogin_missingLoginCode_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/mini/phone-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"phoneCode":"phone-code"}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));

		verify(authService, never()).phoneLogin(anyString(), anyString());
	}

	@Test
	@DisplayName("returns 400 when phoneCode is missing")
	void phoneLogin_missingPhoneCode_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/mini/phone-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"loginCode":"login-code"}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));

		verify(authService, never()).phoneLogin(anyString(), anyString());
	}
}
