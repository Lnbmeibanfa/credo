package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.dto.contract.BreachClauseDto;
import com.credo.credo_server.dto.contract.BreachClauseRequest;
import com.credo.credo_server.dto.contract.SleepContractDto;
import com.credo.credo_server.dto.contract.SleepContractUpsertRequest;
import com.credo.credo_server.entity.Contract;
import com.credo.credo_server.entity.ContractBreachClause;
import com.credo.credo_server.entity.SleepContract;
import com.credo.credo_server.mapper.ContractBreachClauseMapper;
import com.credo.credo_server.mapper.ContractMapper;
import com.credo.credo_server.mapper.SleepContractMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class SleepContractService {

	public static final String CONTRACT_TYPE_SLEEP = "SLEEP";
	public static final String CLAUSE_RECORD = "RECORD";
	public static final String CLAUSE_REVIEW = "REVIEW";
	public static final String CLAUSE_CUSTOM = "CUSTOM";

	private static final int STATUS_ACTIVE = 1;
	private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final DateTimeFormatter SIGNED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final ContractMapper contractMapper;
	private final SleepContractMapper sleepContractMapper;
	private final ContractBreachClauseMapper contractBreachClauseMapper;

	public SleepContractService(
		ContractMapper contractMapper,
		SleepContractMapper sleepContractMapper,
		ContractBreachClauseMapper contractBreachClauseMapper
	) {
		this.contractMapper = contractMapper;
		this.sleepContractMapper = sleepContractMapper;
		this.contractBreachClauseMapper = contractBreachClauseMapper;
	}

	public SleepContractDto getMine(Long userId) {
		Contract contract = findSleepContract(userId);
		if (contract == null) {
			return null;
		}
		return toDto(contract);
	}

	@Transactional
	public SleepContractDto upsert(Long userId, SleepContractUpsertRequest request) {
		ValidatedUpsert validated = validateRequest(request);
		Contract existing = findSleepContract(userId);
		LocalDateTime signedAt = LocalDateTime.now();

		if (existing == null) {
			Contract contract = new Contract();
			contract.setUserId(userId);
			contract.setType(CONTRACT_TYPE_SLEEP);
			contract.setContractNo(generateContractNo(LocalDate.now()));
			contract.setStatus(STATUS_ACTIVE);
			contract.setStartDate(validated.startDate());
			contract.setEndDate(validated.endDate());
			contract.setSignedAt(signedAt);
			contractMapper.insert(contract);

			SleepContract sleepContract = new SleepContract();
			sleepContract.setContractId(contract.getId());
			sleepContract.setTargetBedtime(validated.targetBedtime());
			sleepContractMapper.insert(sleepContract);

			insertClauses(contract.getId(), validated.clauses());
			return toDto(contract);
		}

		existing.setStartDate(validated.startDate());
		existing.setEndDate(validated.endDate());
		existing.setSignedAt(signedAt);
		contractMapper.updateById(existing);

		SleepContract sleepContract = sleepContractMapper.selectById(existing.getId());
		if (sleepContract == null) {
			sleepContract = new SleepContract();
			sleepContract.setContractId(existing.getId());
			sleepContract.setTargetBedtime(validated.targetBedtime());
			sleepContractMapper.insert(sleepContract);
		} else {
			sleepContract.setTargetBedtime(validated.targetBedtime());
			sleepContractMapper.updateById(sleepContract);
		}

		contractBreachClauseMapper.delete(
			new LambdaQueryWrapper<ContractBreachClause>().eq(ContractBreachClause::getContractId, existing.getId())
		);
		insertClauses(existing.getId(), validated.clauses());
		return toDto(existing);
	}

	private Contract findSleepContract(Long userId) {
		return contractMapper.selectOne(
			new LambdaQueryWrapper<Contract>()
				.eq(Contract::getUserId, userId)
				.eq(Contract::getType, CONTRACT_TYPE_SLEEP)
		);
	}

	private ValidatedUpsert validateRequest(SleepContractUpsertRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER);
		}

		String targetBedtimeRaw = request.getTargetBedtime();
		if (targetBedtimeRaw == null || targetBedtimeRaw.isBlank() || !TIME_PATTERN.matcher(targetBedtimeRaw).matches()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "targetBedtime is required");
		}

		LocalDate startDate = parseDate(request.getStartDate(), "startDate");
		LocalDate endDate = parseDate(request.getEndDate(), "endDate");
		if (startDate.isAfter(endDate)) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "startDate must be on or before endDate");
		}

		List<BreachClauseRequest> clauses = request.getBreachClauses();
		if (clauses == null || clauses.isEmpty()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "breachClauses is required");
		}

		boolean recordEnabled = clauses.stream()
			.anyMatch(clause -> CLAUSE_RECORD.equalsIgnoreCase(clause.getType()) && Boolean.TRUE.equals(clause.getEnabled()));
		if (!recordEnabled) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, "RECORD clause must be enabled");
		}

		LocalTime targetBedtime = LocalTime.parse(targetBedtimeRaw);
		return new ValidatedUpsert(targetBedtime, startDate, endDate, clauses);
	}

	private LocalDate parseDate(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, fieldName + " is required");
		}
		try {
			return LocalDate.parse(value, DATE_FORMAT);
		} catch (DateTimeParseException ex) {
			throw new BusinessException(ErrorCode.INVALID_PARAMETER, fieldName + " must be YYYY-MM-DD");
		}
	}

	private void insertClauses(Long contractId, List<BreachClauseRequest> clauses) {
		int sortOrder = 0;
		for (BreachClauseRequest clause : clauses) {
			if (clause.getType() == null || clause.getType().isBlank()) {
				continue;
			}
			String type = clause.getType().toUpperCase(Locale.ROOT);
			boolean enabled = CLAUSE_RECORD.equals(type) || Boolean.TRUE.equals(clause.getEnabled());

			ContractBreachClause row = new ContractBreachClause();
			row.setContractId(contractId);
			row.setClauseType(type);
			row.setEnabled(enabled ? 1 : 0);
			row.setContentText(clause.getContentText());
			row.setSortOrder(sortOrder++);
			contractBreachClauseMapper.insert(row);
		}
	}

	String generateContractNo(LocalDate today) {
		String datePart = today.format(DateTimeFormatter.BASIC_ISO_DATE);
		String prefix = "C-" + datePart + "-";
		Contract last = contractMapper.selectOne(
			new LambdaQueryWrapper<Contract>()
				.likeRight(Contract::getContractNo, prefix)
				.orderByDesc(Contract::getContractNo)
				.last("LIMIT 1")
		);

		int seq = 1;
		if (last != null) {
			String suffix = last.getContractNo().substring(prefix.length());
			seq = Integer.parseInt(suffix) + 1;
		}
		return prefix + String.format("%03d", seq);
	}

	private SleepContractDto toDto(Contract contract) {
		SleepContract sleepContract = sleepContractMapper.selectById(contract.getId());
		List<ContractBreachClause> clauses = contractBreachClauseMapper.selectList(
			new LambdaQueryWrapper<ContractBreachClause>()
				.eq(ContractBreachClause::getContractId, contract.getId())
				.orderByAsc(ContractBreachClause::getSortOrder)
		);

		SleepContractDto dto = new SleepContractDto();
		dto.setId(contract.getId());
		dto.setContractNo(contract.getContractNo());
		if (sleepContract != null && sleepContract.getTargetBedtime() != null) {
			dto.setTargetBedtime(sleepContract.getTargetBedtime().format(DateTimeFormatter.ofPattern("HH:mm")));
		}
		dto.setStartDate(contract.getStartDate().format(DATE_FORMAT));
		dto.setEndDate(contract.getEndDate().format(DATE_FORMAT));
		dto.setSignedAt(contract.getSignedAt() != null ? contract.getSignedAt().format(SIGNED_AT_FORMAT) : null);

		List<BreachClauseDto> clauseDtos = new ArrayList<>();
		clauses.stream()
			.sorted(Comparator.comparing(ContractBreachClause::getSortOrder))
			.forEach(clause -> clauseDtos.add(new BreachClauseDto(
				clause.getClauseType(),
				clause.getEnabled() != null && clause.getEnabled() == 1,
				clause.getContentText()
			)));
		dto.setBreachClauses(clauseDtos);
		return dto;
	}

	private record ValidatedUpsert(LocalTime targetBedtime, LocalDate startDate, LocalDate endDate, List<BreachClauseRequest> clauses) {
	}
}
