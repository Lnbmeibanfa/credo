package com.credo.credo_server.controller;

import com.credo.credo_server.common.ApiResponse;
import com.credo.credo_server.dto.contract.SleepContractDto;
import com.credo.credo_server.dto.contract.SleepContractUpsertRequest;
import com.credo.credo_server.service.SleepContractService;
import com.credo.credo_server.support.AuthTokenSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts/sleep")
public class ContractController {

	private final SleepContractService sleepContractService;
	private final AuthTokenSupport authTokenSupport;

	public ContractController(SleepContractService sleepContractService, AuthTokenSupport authTokenSupport) {
		this.sleepContractService = sleepContractService;
		this.authTokenSupport = authTokenSupport;
	}

	@GetMapping("/mine")
	public ApiResponse<SleepContractDto> getMine(@RequestHeader(value = "Authorization", required = false) String authorization) {
		Long userId = authTokenSupport.requireUserId(authorization);
		SleepContractDto contract = sleepContractService.getMine(userId);
		return ApiResponse.success(contract);
	}

	@PutMapping
	public ApiResponse<SleepContractDto> upsert(
		@RequestHeader(value = "Authorization", required = false) String authorization,
		@RequestBody SleepContractUpsertRequest request
	) {
		Long userId = authTokenSupport.requireUserId(authorization);
		SleepContractDto contract = sleepContractService.upsert(userId, request);
		return ApiResponse.success(contract);
	}
}
