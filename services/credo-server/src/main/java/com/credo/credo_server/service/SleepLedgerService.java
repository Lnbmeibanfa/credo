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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class SleepLedgerService {

	public static final String EVENT_FULFILLED = "FULFILLED";
	public static final String EVENT_BREACH = "BREACH";
	public static final String STATUS_PENDING = "PENDING";
	public static final String STATUS_UPCOMING = "UPCOMING";
	public static final String STATUS_FULFILLED = "FULFILLED";
	public static final String STATUS_BREACH = "BREACH";

	private static final Set<String> ALLOWED_STATUSES = Set.of(
		STATUS_PENDING,
		STATUS_UPCOMING,
		STATUS_FULFILLED,
		STATUS_BREACH
	);

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final DateTimeFormatter CREATED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final ContractMapper contractMapper;
	private final SleepLedgerEventMapper sleepLedgerEventMapper;
	private final Clock clock;

	public SleepLedgerService(
		ContractMapper contractMapper,
		SleepLedgerEventMapper sleepLedgerEventMapper,
		Clock clock
	) {
		this.contractMapper = contractMapper;
		this.sleepLedgerEventMapper = sleepLedgerEventMapper;
		this.clock = clock;
	}

	@Transactional
	public SleepLedgerEventDto record(Long userId, SleepLedgerRecordRequest request) {
		Contract contract = requireSleepContract(userId);
		LocalDate recordDate = parseRecordDate(request);
		String eventType = validateEventType(request);

		validateRecordDateInRange(recordDate, contract);
		validateNotFuture(recordDate);

		SleepLedgerEvent existing = sleepLedgerEventMapper.selectOne(
			new LambdaQueryWrapper<SleepLedgerEvent>()
				.eq(SleepLedgerEvent::getContractId, contract.getId())
				.eq(SleepLedgerEvent::getRecordDate, recordDate)
		);
		if (existing != null) {
			throw new BusinessException(ErrorCode.DUPLICATE_RECORD);
		}

		SleepLedgerEvent event = new SleepLedgerEvent();
		event.setUserId(userId);
		event.setContractId(contract.getId());
		event.setRecordDate(recordDate);
		event.setEventType(eventType);
		event.setNote(request.getNote());
		sleepLedgerEventMapper.insert(event);

		return toEventDto(event);
	}

	public SleepDailyViewDto getDailyView(Long userId, LocalDate from, LocalDate to, String status) {
		Contract contract = requireSleepContract(userId);
		LocalDate rangeStart = from != null ? from : contract.getStartDate();
		LocalDate rangeEnd = to != null ? to : contract.getEndDate();

		if (rangeStart.isBefore(contract.getStartDate())) {
			rangeStart = contract.getStartDate();
		}
		if (rangeEnd.isAfter(contract.getEndDate())) {
			rangeEnd = contract.getEndDate();
		}
		if (rangeStart.isAfter(rangeEnd)) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid date range");
		}

		Map<LocalDate, SleepLedgerEvent> eventsByDate = loadEventsInRange(contract.getId(), rangeStart, rangeEnd);
		List<SleepDailyDayDto> days = buildDailyDays(rangeStart, rangeEnd, eventsByDate);
		days = filterDaysByStatus(days, status);

		SleepDailyViewDto view = new SleepDailyViewDto();
		view.setContractId(contract.getId());
		view.setSummary(buildSummary(contract));
		view.setDays(days);
		return view;
	}

	public SleepLedgerSummaryDto getSummary(Long userId) {
		Contract contract = requireSleepContract(userId);
		return buildSummary(contract);
	}

	private Contract requireSleepContract(Long userId) {
		Contract contract = contractMapper.selectOne(
			new LambdaQueryWrapper<Contract>()
				.eq(Contract::getUserId, userId)
				.eq(Contract::getType, SleepContractService.CONTRACT_TYPE_SLEEP)
		);
		if (contract == null) {
			throw new BusinessException(ErrorCode.NO_CONTRACT);
		}
		return contract;
	}

	private LocalDate parseRecordDate(SleepLedgerRecordRequest request) {
		if (request == null || request.getRecordDate() == null || request.getRecordDate().isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "recordDate is required");
		}
		try {
			return LocalDate.parse(request.getRecordDate(), DATE_FORMAT);
		} catch (DateTimeParseException ex) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "recordDate must be YYYY-MM-DD");
		}
	}

	private String validateEventType(SleepLedgerRecordRequest request) {
		if (request.getEventType() == null || request.getEventType().isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "eventType is required");
		}
		String eventType = request.getEventType().toUpperCase(Locale.ROOT);
		if (!EVENT_FULFILLED.equals(eventType) && !EVENT_BREACH.equals(eventType)) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "eventType must be FULFILLED or BREACH");
		}
		return eventType;
	}

	private void validateRecordDateInRange(LocalDate recordDate, Contract contract) {
		if (recordDate.isBefore(contract.getStartDate()) || recordDate.isAfter(contract.getEndDate())) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "recordDate out of contract range");
		}
	}

	private void validateNotFuture(LocalDate recordDate) {
		if (recordDate.isAfter(today())) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "recordDate cannot be in the future");
		}
	}

	List<SleepDailyDayDto> filterDaysByStatus(List<SleepDailyDayDto> days, String status) {
		if (status == null || status.isBlank()) {
			return days;
		}

		Set<String> allowedStatuses = parseStatusFilter(status);
		List<SleepDailyDayDto> filtered = new ArrayList<>();
		for (SleepDailyDayDto day : days) {
			if (allowedStatuses.contains(day.getStatus())) {
				filtered.add(day);
			}
		}
		return filtered;
	}

	Set<String> parseStatusFilter(String status) {
		String[] parts = status.split(",");
		Set<String> allowedStatuses = new HashSet<>();
		for (String part : parts) {
			String normalized = part.trim().toUpperCase(Locale.ROOT);
			if (normalized.isEmpty()) {
				continue;
			}
			if (!ALLOWED_STATUSES.contains(normalized)) {
				throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid status filter");
			}
			allowedStatuses.add(normalized);
		}
		if (allowedStatuses.isEmpty()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid status filter");
		}
		return allowedStatuses;
	}

	private Map<LocalDate, SleepLedgerEvent> loadEventsInRange(Long contractId, LocalDate from, LocalDate to) {
		List<SleepLedgerEvent> events = sleepLedgerEventMapper.selectList(
			new LambdaQueryWrapper<SleepLedgerEvent>()
				.eq(SleepLedgerEvent::getContractId, contractId)
				.ge(SleepLedgerEvent::getRecordDate, from)
				.le(SleepLedgerEvent::getRecordDate, to)
		);
		Map<LocalDate, SleepLedgerEvent> map = new HashMap<>();
		for (SleepLedgerEvent event : events) {
			map.put(event.getRecordDate(), event);
		}
		return map;
	}

	private List<SleepLedgerEvent> loadRecordedEventsUpToToday(Contract contract) {
		LocalDate today = today();
		LocalDate end = contract.getEndDate().isBefore(today) ? contract.getEndDate() : today;
		if (contract.getStartDate().isAfter(end)) {
			return List.of();
		}
		return sleepLedgerEventMapper.selectList(
			new LambdaQueryWrapper<SleepLedgerEvent>()
				.eq(SleepLedgerEvent::getContractId, contract.getId())
				.ge(SleepLedgerEvent::getRecordDate, contract.getStartDate())
				.le(SleepLedgerEvent::getRecordDate, end)
		);
	}

	List<SleepDailyDayDto> buildDailyDays(LocalDate from, LocalDate to, Map<LocalDate, SleepLedgerEvent> eventsByDate) {
		LocalDate today = today();
		List<SleepDailyDayDto> days = new ArrayList<>();
		for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
			SleepDailyDayDto day = new SleepDailyDayDto();
			day.setRecordDate(date.format(DATE_FORMAT));

			SleepLedgerEvent event = eventsByDate.get(date);
			if (event != null) {
				day.setStatus(event.getEventType());
				day.setEventType(event.getEventType());
				if (event.getCreatedAt() != null) {
					day.setCreatedAt(event.getCreatedAt().format(CREATED_AT_FORMAT));
				}
			} else if (date.isAfter(today)) {
				day.setStatus(STATUS_UPCOMING);
			} else {
				day.setStatus(STATUS_PENDING);
			}
			days.add(day);
		}
		return days;
	}

	SleepLedgerSummaryDto buildSummary(Contract contract) {
		LocalDate today = today();
		LocalDate obligationEnd = contract.getEndDate().isBefore(today) ? contract.getEndDate() : today;
		int obligationDays = 0;
		if (!contract.getStartDate().isAfter(obligationEnd)) {
			obligationDays = (int) ChronoUnit.DAYS.between(contract.getStartDate(), obligationEnd) + 1;
		}

		List<SleepLedgerEvent> recordedEvents = loadRecordedEventsUpToToday(contract);
		int fulfilledDays = 0;
		int breachDays = 0;
		for (SleepLedgerEvent event : recordedEvents) {
			if (EVENT_FULFILLED.equals(event.getEventType())) {
				fulfilledDays++;
			} else if (EVENT_BREACH.equals(event.getEventType())) {
				breachDays++;
			}
		}

		int recordedDays = recordedEvents.size();
		SleepLedgerSummaryDto summary = new SleepLedgerSummaryDto();
		summary.setObligationDays(obligationDays);
		summary.setRecordedDays(recordedDays);
		summary.setPendingDays(Math.max(0, obligationDays - recordedDays));
		summary.setFulfilledDays(fulfilledDays);
		summary.setBreachDays(breachDays);
		return summary;
	}

	private SleepLedgerEventDto toEventDto(SleepLedgerEvent event) {
		SleepLedgerEventDto dto = new SleepLedgerEventDto();
		dto.setId(event.getId());
		dto.setContractId(event.getContractId());
		dto.setRecordDate(event.getRecordDate().format(DATE_FORMAT));
		dto.setEventType(event.getEventType());
		dto.setNote(event.getNote());
		if (event.getCreatedAt() != null) {
			dto.setCreatedAt(event.getCreatedAt().format(CREATED_AT_FORMAT));
		}
		return dto;
	}

	LocalDate today() {
		return LocalDate.now(clock);
	}
}
