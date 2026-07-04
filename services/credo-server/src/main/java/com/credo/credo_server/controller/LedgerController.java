package com.credo.credo_server.controller;

import com.credo.credo_server.common.ApiResponse;
import com.credo.credo_server.dto.ledger.SleepDailyViewDto;
import com.credo.credo_server.dto.ledger.SleepLedgerEventDto;
import com.credo.credo_server.dto.ledger.SleepLedgerRecordRequest;
import com.credo.credo_server.dto.ledger.SleepLedgerSummaryDto;
import com.credo.credo_server.service.SleepLedgerService;
import com.credo.credo_server.support.AuthTokenSupport;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ledger/sleep")
public class LedgerController {

	private final SleepLedgerService sleepLedgerService;
	private final AuthTokenSupport authTokenSupport;

	public LedgerController(SleepLedgerService sleepLedgerService, AuthTokenSupport authTokenSupport) {
		this.sleepLedgerService = sleepLedgerService;
		this.authTokenSupport = authTokenSupport;
	}

	@PostMapping("/records")
	public ApiResponse<SleepLedgerEventDto> record(
		@RequestHeader(value = "Authorization", required = false) String authorization,
		@RequestBody SleepLedgerRecordRequest request
	) {
		Long userId = authTokenSupport.requireUserId(authorization);
		SleepLedgerEventDto event = sleepLedgerService.record(userId, request);
		return ApiResponse.success(event);
	}

	@GetMapping("/daily-view")
	public ApiResponse<SleepDailyViewDto> getDailyView(
		@RequestHeader(value = "Authorization", required = false) String authorization,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
		@RequestParam(required = false) String status
	) {
		Long userId = authTokenSupport.requireUserId(authorization);
		SleepDailyViewDto view = sleepLedgerService.getDailyView(userId, from, to, status);
		return ApiResponse.success(view);
	}

	@GetMapping("/summary")
	public ApiResponse<SleepLedgerSummaryDto> getSummary(
		@RequestHeader(value = "Authorization", required = false) String authorization
	) {
		Long userId = authTokenSupport.requireUserId(authorization);
		SleepLedgerSummaryDto summary = sleepLedgerService.getSummary(userId);
		return ApiResponse.success(summary);
	}
}
