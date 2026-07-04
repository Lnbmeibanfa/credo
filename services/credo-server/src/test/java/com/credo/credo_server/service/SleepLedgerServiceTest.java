package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.dto.ledger.SleepDailyDayDto;
import com.credo.credo_server.dto.ledger.SleepDailyViewDto;
import com.credo.credo_server.dto.ledger.SleepLedgerEventDto;
import com.credo.credo_server.dto.ledger.SleepLedgerRecordRequest;
import com.credo.credo_server.dto.ledger.SleepLedgerSummaryDto;
import com.credo.credo_server.entity.Contract;
import com.credo.credo_server.entity.SleepLedgerEvent;
import com.credo.credo_server.mapper.ContractMapper;
import com.credo.credo_server.mapper.SleepLedgerEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SleepLedgerServiceTest {

	private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

	@Mock
	private ContractMapper contractMapper;

	@Mock
	private SleepLedgerEventMapper sleepLedgerEventMapper;

	private SleepLedgerService sleepLedgerService;

	private Contract contract;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(Instant.parse("2026-06-28T12:00:00+08:00"), ZONE);
		sleepLedgerService = new SleepLedgerService(contractMapper, sleepLedgerEventMapper, clock);

		contract = new Contract();
		contract.setId(10L);
		contract.setUserId(1L);
		contract.setType(SleepContractService.CONTRACT_TYPE_SLEEP);
		contract.setStartDate(LocalDate.of(2026, 6, 24));
		contract.setEndDate(LocalDate.of(2026, 6, 30));
	}

	@Test
	@DisplayName("records fulfilled event for eligible date")
	void record_success_insertsEvent() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		when(sleepLedgerEventMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
		when(sleepLedgerEventMapper.insert(any(SleepLedgerEvent.class))).thenAnswer(invocation -> {
			SleepLedgerEvent event = invocation.getArgument(0);
			event.setId(100L);
			event.setCreatedAt(LocalDateTime.of(2026, 6, 28, 12, 0));
			return 1;
		});

		SleepLedgerRecordRequest request = new SleepLedgerRecordRequest();
		request.setRecordDate("2026-06-28");
		request.setEventType("FULFILLED");

		SleepLedgerEventDto result = sleepLedgerService.record(1L, request);

		assertThat(result.getEventType()).isEqualTo("FULFILLED");
		assertThat(result.getRecordDate()).isEqualTo("2026-06-28");

		ArgumentCaptor<SleepLedgerEvent> captor = ArgumentCaptor.forClass(SleepLedgerEvent.class);
		verify(sleepLedgerEventMapper).insert(captor.capture());
		assertThat(captor.getValue().getContractId()).isEqualTo(10L);
	}

	@Test
	@DisplayName("rejects duplicate record for same date")
	void record_duplicate_throwsConflict() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent existing = new SleepLedgerEvent();
		existing.setId(99L);
		when(sleepLedgerEventMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

		SleepLedgerRecordRequest request = new SleepLedgerRecordRequest();
		request.setRecordDate("2026-06-28");
		request.setEventType("BREACH");

		assertThatThrownBy(() -> sleepLedgerService.record(1L, request))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.DUPLICATE_RECORD);

		verify(sleepLedgerEventMapper, never()).insert(any(SleepLedgerEvent.class));
	}

	@Test
	@DisplayName("rejects future record date")
	void record_futureDate_throwsInvalidParameter() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);

		SleepLedgerRecordRequest request = new SleepLedgerRecordRequest();
		request.setRecordDate("2026-06-29");
		request.setEventType("FULFILLED");

		assertThatThrownBy(() -> sleepLedgerService.record(1L, request))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	@DisplayName("rejects when no sleep contract exists")
	void record_noContract_throwsNotFound() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

		SleepLedgerRecordRequest request = new SleepLedgerRecordRequest();
		request.setRecordDate("2026-06-28");
		request.setEventType("FULFILLED");

		assertThatThrownBy(() -> sleepLedgerService.record(1L, request))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.NO_CONTRACT);
	}

	@Test
	@DisplayName("daily view marks pending and upcoming days")
	void getDailyView_mixedStatuses() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent fulfilled = new SleepLedgerEvent();
		fulfilled.setRecordDate(LocalDate.of(2026, 6, 26));
		fulfilled.setEventType(SleepLedgerService.EVENT_FULFILLED);
		fulfilled.setCreatedAt(LocalDateTime.of(2026, 6, 27, 8, 0));
		when(sleepLedgerEventMapper.selectList(any(LambdaQueryWrapper.class)))
			.thenReturn(List.of(fulfilled))
			.thenReturn(List.of(fulfilled));

		SleepDailyViewDto view = sleepLedgerService.getDailyView(1L, null, null, null);

		assertThat(view.getContractId()).isEqualTo(10L);
		assertThat(view.getDays()).hasSize(7);
		assertThat(findDay(view.getDays(), "2026-06-26").getStatus()).isEqualTo("FULFILLED");
		assertThat(findDay(view.getDays(), "2026-06-27").getStatus()).isEqualTo("PENDING");
		assertThat(findDay(view.getDays(), "2026-06-29").getStatus()).isEqualTo("UPCOMING");
	}

	@Test
	@DisplayName("summary counts obligation and pending days")
	void getSummary_countsCorrectly() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent fulfilled = new SleepLedgerEvent();
		fulfilled.setEventType(SleepLedgerService.EVENT_FULFILLED);
		fulfilled.setRecordDate(LocalDate.of(2026, 6, 26));
		SleepLedgerEvent breach = new SleepLedgerEvent();
		breach.setEventType(SleepLedgerService.EVENT_BREACH);
		breach.setRecordDate(LocalDate.of(2026, 6, 25));
		when(sleepLedgerEventMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(fulfilled, breach));

		SleepLedgerSummaryDto summary = sleepLedgerService.getSummary(1L);

		assertThat(summary.getObligationDays()).isEqualTo(5);
		assertThat(summary.getRecordedDays()).isEqualTo(2);
		assertThat(summary.getPendingDays()).isEqualTo(3);
		assertThat(summary.getFulfilledDays()).isEqualTo(1);
		assertThat(summary.getBreachDays()).isEqualTo(1);
	}

	@Test
	@DisplayName("buildDailyDays computes statuses from map")
	void buildDailyDays_fromMap() {
		Map<java.time.LocalDate, SleepLedgerEvent> events = new HashMap<>();
		SleepLedgerEvent event = new SleepLedgerEvent();
		event.setEventType(SleepLedgerService.EVENT_BREACH);
		event.setCreatedAt(LocalDateTime.of(2026, 6, 28, 9, 0));
		events.put(LocalDate.of(2026, 6, 28), event);

		List<SleepDailyDayDto> days = sleepLedgerService.buildDailyDays(
			LocalDate.of(2026, 6, 27),
			LocalDate.of(2026, 6, 29),
			events
		);

		assertThat(days).hasSize(3);
		assertThat(days.get(0).getStatus()).isEqualTo("PENDING");
		assertThat(days.get(1).getStatus()).isEqualTo("BREACH");
		assertThat(days.get(2).getStatus()).isEqualTo("UPCOMING");
	}

	@Test
	@DisplayName("daily view filters pending days only")
	void getDailyView_statusPending_onlyPendingDays() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent fulfilled = new SleepLedgerEvent();
		fulfilled.setRecordDate(LocalDate.of(2026, 6, 26));
		fulfilled.setEventType(SleepLedgerService.EVENT_FULFILLED);
		when(sleepLedgerEventMapper.selectList(any(LambdaQueryWrapper.class)))
			.thenReturn(List.of(fulfilled))
			.thenReturn(List.of(fulfilled));

		SleepDailyViewDto view = sleepLedgerService.getDailyView(
			1L,
			null,
			LocalDate.of(2026, 6, 28),
			"PENDING"
		);

		assertThat(view.getDays()).extracting(SleepDailyDayDto::getRecordDate)
			.containsExactly("2026-06-24", "2026-06-25", "2026-06-27", "2026-06-28");
		assertThat(view.getDays()).allMatch(day -> "PENDING".equals(day.getStatus()));
	}

	@Test
	@DisplayName("daily view filters multiple statuses")
	void getDailyView_multiStatus_fulfilledAndBreach() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent fulfilled = new SleepLedgerEvent();
		fulfilled.setRecordDate(LocalDate.of(2026, 6, 26));
		fulfilled.setEventType(SleepLedgerService.EVENT_FULFILLED);
		SleepLedgerEvent breach = new SleepLedgerEvent();
		breach.setRecordDate(LocalDate.of(2026, 6, 25));
		breach.setEventType(SleepLedgerService.EVENT_BREACH);
		when(sleepLedgerEventMapper.selectList(any(LambdaQueryWrapper.class)))
			.thenReturn(List.of(fulfilled, breach))
			.thenReturn(List.of(fulfilled, breach));

		SleepDailyViewDto view = sleepLedgerService.getDailyView(1L, null, null, "FULFILLED,BREACH");

		assertThat(view.getDays()).hasSize(2);
		assertThat(view.getDays()).extracting(SleepDailyDayDto::getStatus)
			.containsExactlyInAnyOrder("FULFILLED", "BREACH");
	}

	@Test
	@DisplayName("daily view rejects invalid status filter")
	void getDailyView_invalidStatus_throwsInvalidParameter() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);

		assertThatThrownBy(() -> sleepLedgerService.getDailyView(1L, null, null, "INVALID"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	@DisplayName("filtered daily view preserves unfiltered summary")
	void getDailyView_statusPending_preservesSummary() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contract);
		SleepLedgerEvent fulfilled = new SleepLedgerEvent();
		fulfilled.setRecordDate(LocalDate.of(2026, 6, 26));
		fulfilled.setEventType(SleepLedgerService.EVENT_FULFILLED);
		SleepLedgerEvent breach = new SleepLedgerEvent();
		breach.setRecordDate(LocalDate.of(2026, 6, 25));
		breach.setEventType(SleepLedgerService.EVENT_BREACH);
		when(sleepLedgerEventMapper.selectList(any(LambdaQueryWrapper.class)))
			.thenReturn(List.of(fulfilled, breach))
			.thenReturn(List.of(fulfilled, breach));

		SleepDailyViewDto view = sleepLedgerService.getDailyView(
			1L,
			null,
			LocalDate.of(2026, 6, 28),
			"PENDING"
		);

		assertThat(view.getSummary().getFulfilledDays()).isEqualTo(1);
		assertThat(view.getSummary().getBreachDays()).isEqualTo(1);
		assertThat(view.getSummary().getPendingDays()).isEqualTo(3);
		assertThat(view.getDays()).allMatch(day -> "PENDING".equals(day.getStatus()));
	}

	private static SleepDailyDayDto findDay(List<SleepDailyDayDto> days, String recordDate) {
		return days.stream()
			.filter(day -> recordDate.equals(day.getRecordDate()))
			.findFirst()
			.orElseThrow();
	}
}
