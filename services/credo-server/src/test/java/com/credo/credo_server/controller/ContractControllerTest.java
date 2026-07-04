package com.credo.credo_server.controller;

import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.common.GlobalExceptionHandler;
import com.credo.credo_server.dto.contract.BreachClauseDto;
import com.credo.credo_server.dto.contract.SleepContractDto;
import com.credo.credo_server.service.SleepContractService;
import com.credo.credo_server.support.AuthTokenSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractController.class)
@Import(GlobalExceptionHandler.class)
class ContractControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private SleepContractService sleepContractService;

	@MockitoBean
	private AuthTokenSupport authTokenSupport;

	@Test
	@DisplayName("GET mine returns sleep contract for authenticated user")
	void getMine_success_returnsContract() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		SleepContractDto dto = sampleDto();
		when(sleepContractService.getMine(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/contracts/sleep/mine")
				.header("Authorization", "Bearer jwt-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.contractNo").value("C-20260624-001"))
			.andExpect(jsonPath("$.data.targetBedtime").value("23:00"));
	}

	@Test
	@DisplayName("GET mine returns null data when no contract")
	void getMine_noContract_returnsNullData() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepContractService.getMine(1L)).thenReturn(null);

		mockMvc.perform(get("/api/contracts/sleep/mine")
				.header("Authorization", "Bearer jwt-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("PUT upsert returns created contract")
	void upsert_success_returnsContract() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepContractService.upsert(eq(1L), any())).thenReturn(sampleDto());

		mockMvc.perform(put("/api/contracts/sleep")
				.header("Authorization", "Bearer jwt-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.contractNo").value("C-20260624-001"));
	}

	@Test
	@DisplayName("returns 401 when Authorization header is missing")
	void getMine_missingAuth_returns401() throws Exception {
		when(authTokenSupport.requireUserId(null))
			.thenThrow(new BusinessException(ErrorCode.UNAUTHORIZED));

		mockMvc.perform(get("/api/contracts/sleep/mine"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

		verify(sleepContractService, never()).getMine(any());
	}

	@Test
	@DisplayName("returns 400 when service rejects invalid payload")
	void upsert_invalidPayload_returns400() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepContractService.upsert(eq(1L), any()))
			.thenThrow(new BusinessException(ErrorCode.INVALID_PARAMETER));

		mockMvc.perform(put("/api/contracts/sleep")
				.header("Authorization", "Bearer jwt-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"startDate":"2026-06-24","endDate":"2026-07-23","breachClauses":[{"type":"RECORD","enabled":true}]}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));
	}

	private static SleepContractDto sampleDto() {
		SleepContractDto dto = new SleepContractDto();
		dto.setId(1L);
		dto.setContractNo("C-20260624-001");
		dto.setTargetBedtime("23:00");
		dto.setStartDate("2026-06-24");
		dto.setEndDate("2026-07-23");
		dto.setSignedAt("2026-06-24T12:00:00");
		dto.setBreachClauses(List.of(new BreachClauseDto("RECORD", true, null)));
		return dto;
	}

	private static String validPayload() {
		return """
			{
			  "targetBedtime":"23:00",
			  "startDate":"2026-06-24",
			  "endDate":"2026-07-23",
			  "breachClauses":[
			    {"type":"RECORD","enabled":true},
			    {"type":"REVIEW","enabled":true},
			    {"type":"CUSTOM","enabled":true,"contentText":"连续 3 次违约"}
			  ]
			}
			""";
	}
}
