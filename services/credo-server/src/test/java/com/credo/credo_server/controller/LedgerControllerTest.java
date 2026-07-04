package com.credo.credo_server.controller;

import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.common.GlobalExceptionHandler;
import com.credo.credo_server.dto.ledger.SleepDailyDayDto;
import com.credo.credo_server.dto.ledger.SleepDailyViewDto;
import com.credo.credo_server.dto.ledger.SleepLedgerEventDto;
import com.credo.credo_server.dto.ledger.SleepLedgerSummaryDto;
import com.credo.credo_server.service.SleepLedgerService;
import com.credo.credo_server.support.AuthTokenSupport;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LedgerController.class)
@Import(GlobalExceptionHandler.class)
class LedgerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SleepLedgerService sleepLedgerService;

	@MockitoBean
	private AuthTokenSupport authTokenSupport;

	@Test
	@DisplayName("POST record returns event")
	void record_success() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		SleepLedgerEventDto dto = new SleepLedgerEventDto();
		dto.setId(1L);
		dto.setContractId(10L);
		dto.setRecordDate("2026-06-28");
		dto.setEventType("FULFILLED");
		when(sleepLedgerService.record(eq(1L), any())).thenReturn(dto);

		mockMvc.perform(post("/api/ledger/sleep/records")
				.header("Authorization", "Bearer jwt-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"recordDate":"2026-06-28","eventType":"FULFILLED"}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.eventType").value("FULFILLED"));
	}

	@Test
	@DisplayName("GET daily-view returns days")
	void getDailyView_success() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		SleepDailyDayDto day = new SleepDailyDayDto();
		day.setRecordDate("2026-06-28");
		day.setStatus("PENDING");
		SleepLedgerSummaryDto summary = new SleepLedgerSummaryDto();
		summary.setObligationDays(5);
		SleepDailyViewDto view = new SleepDailyViewDto();
		view.setContractId(10L);
		view.setSummary(summary);
		view.setDays(List.of(day));
		when(sleepLedgerService.getDailyView(eq(1L), isNull(), isNull(), isNull())).thenReturn(view);

		mockMvc.perform(get("/api/ledger/sleep/daily-view")
				.header("Authorization", "Bearer jwt-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.contractId").value(10))
			.andExpect(jsonPath("$.data.days[0].status").value("PENDING"));
	}

	@Test
	@DisplayName("GET daily-view forwards status filter")
	void getDailyView_withStatusPending() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		SleepDailyDayDto day = new SleepDailyDayDto();
		day.setRecordDate("2026-06-28");
		day.setStatus("PENDING");
		SleepDailyViewDto view = new SleepDailyViewDto();
		view.setContractId(10L);
		view.setDays(List.of(day));
		when(sleepLedgerService.getDailyView(eq(1L), isNull(), eq(java.time.LocalDate.of(2026, 6, 28)), eq("PENDING")))
			.thenReturn(view);

		mockMvc.perform(get("/api/ledger/sleep/daily-view")
				.header("Authorization", "Bearer jwt-token")
				.param("status", "PENDING")
				.param("to", "2026-06-28"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.days[0].status").value("PENDING"));
	}

	@Test
	@DisplayName("GET daily-view returns 400 for invalid status")
	void getDailyView_invalidStatus_returns400() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepLedgerService.getDailyView(eq(1L), isNull(), isNull(), eq("INVALID")))
			.thenThrow(new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid status filter"));

		mockMvc.perform(get("/api/ledger/sleep/daily-view")
				.header("Authorization", "Bearer jwt-token")
				.param("status", "INVALID"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));
	}

	@Test
	@DisplayName("GET summary returns counts")
	void getSummary_success() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		SleepLedgerSummaryDto summary = new SleepLedgerSummaryDto();
		summary.setObligationDays(7);
		summary.setPendingDays(3);
		when(sleepLedgerService.getSummary(1L)).thenReturn(summary);

		mockMvc.perform(get("/api/ledger/sleep/summary")
				.header("Authorization", "Bearer jwt-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.obligationDays").value(7))
			.andExpect(jsonPath("$.data.pendingDays").value(3));
	}

	@Test
	@DisplayName("returns 409 for duplicate record")
	void record_duplicate_returns409() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepLedgerService.record(eq(1L), any()))
			.thenThrow(new BusinessException(ErrorCode.DUPLICATE_RECORD));

		mockMvc.perform(post("/api/ledger/sleep/records")
				.header("Authorization", "Bearer jwt-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"recordDate":"2026-06-28","eventType":"BREACH"}
					"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("DUPLICATE_RECORD"));
	}

	@Test
	@DisplayName("returns 404 when no contract")
	void getSummary_noContract_returns404() throws Exception {
		when(authTokenSupport.requireUserId("Bearer jwt-token")).thenReturn(1L);
		when(sleepLedgerService.getSummary(1L)).thenThrow(new BusinessException(ErrorCode.NO_CONTRACT));

		mockMvc.perform(get("/api/ledger/sleep/summary")
				.header("Authorization", "Bearer jwt-token"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("NO_CONTRACT"));
	}

	@Test
	@DisplayName("returns 401 when Authorization header is missing")
	void record_missingAuth_returns401() throws Exception {
		when(authTokenSupport.requireUserId(null)).thenThrow(new BusinessException(ErrorCode.UNAUTHORIZED));

		mockMvc.perform(post("/api/ledger/sleep/records")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"recordDate":"2026-06-28","eventType":"FULFILLED"}
					"""))
			.andExpect(status().isUnauthorized());

		verify(sleepLedgerService, never()).record(any(), any());
	}
}
