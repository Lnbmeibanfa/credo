package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.dto.contract.BreachClauseRequest;
import com.credo.credo_server.dto.contract.SleepContractDto;
import com.credo.credo_server.dto.contract.SleepContractUpsertRequest;
import com.credo.credo_server.entity.Contract;
import com.credo.credo_server.entity.ContractBreachClause;
import com.credo.credo_server.entity.SleepContract;
import com.credo.credo_server.mapper.ContractBreachClauseMapper;
import com.credo.credo_server.mapper.ContractMapper;
import com.credo.credo_server.mapper.SleepContractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SleepContractServiceTest {

	@Mock
	private ContractMapper contractMapper;

	@Mock
	private SleepContractMapper sleepContractMapper;

	@Mock
	private ContractBreachClauseMapper contractBreachClauseMapper;

	@InjectMocks
	private SleepContractService sleepContractService;

	private SleepContractUpsertRequest validRequest;

	@BeforeEach
	void setUp() {
		validRequest = new SleepContractUpsertRequest();
		validRequest.setTargetBedtime("23:00");
		validRequest.setStartDate("2026-06-24");
		validRequest.setEndDate("2026-07-23");
		validRequest.setBreachClauses(List.of(
			clause(SleepContractService.CLAUSE_RECORD, true, null),
			clause(SleepContractService.CLAUSE_REVIEW, true, null),
			clause(SleepContractService.CLAUSE_CUSTOM, true, "连续 3 次违约")
		));
	}

	@Test
	@DisplayName("creates first sleep contract for user")
	void upsert_newContract_insertsRows() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null, null);
		when(contractMapper.insert(any(Contract.class))).thenAnswer(invocation -> {
			Contract contract = invocation.getArgument(0);
			contract.setId(10L);
			return 1;
		});
		when(sleepContractMapper.selectById(10L)).thenReturn(sleepContract(10L, LocalTime.of(23, 0)));
		when(contractBreachClauseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
			storedClause(SleepContractService.CLAUSE_RECORD, true, null, 0),
			storedClause(SleepContractService.CLAUSE_REVIEW, true, null, 1),
			storedClause(SleepContractService.CLAUSE_CUSTOM, true, "连续 3 次违约", 2)
		));

		SleepContractDto result = sleepContractService.upsert(1L, validRequest);

		assertThat(result.getContractNo()).startsWith("C-");
		assertThat(result.getTargetBedtime()).isEqualTo("23:00");
		assertThat(result.getStartDate()).isEqualTo("2026-06-24");
		assertThat(result.getEndDate()).isEqualTo("2026-07-23");
		assertThat(result.getBreachClauses()).hasSize(3);

		ArgumentCaptor<Contract> contractCaptor = ArgumentCaptor.forClass(Contract.class);
		verify(contractMapper).insert(contractCaptor.capture());
		assertThat(contractCaptor.getValue().getUserId()).isEqualTo(1L);
		assertThat(contractCaptor.getValue().getType()).isEqualTo(SleepContractService.CONTRACT_TYPE_SLEEP);

		verify(sleepContractMapper).insert(any(SleepContract.class));
		verify(contractBreachClauseMapper, times(3)).insert(any(ContractBreachClause.class));
	}

	@Test
	@DisplayName("updates existing sleep contract and replaces clauses")
	void upsert_existingContract_updatesRows() {
		Contract existing = new Contract();
		existing.setId(20L);
		existing.setUserId(2L);
		existing.setType(SleepContractService.CONTRACT_TYPE_SLEEP);
		existing.setContractNo("C-20260624-001");
		existing.setStartDate(LocalDate.of(2026, 6, 1));
		existing.setEndDate(LocalDate.of(2026, 6, 30));

		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
		when(sleepContractMapper.selectById(20L)).thenReturn(sleepContract(20L, LocalTime.of(23, 0)));
		when(contractBreachClauseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
			storedClause(SleepContractService.CLAUSE_RECORD, true, null, 0)
		));

		SleepContractDto result = sleepContractService.upsert(2L, validRequest);

		assertThat(result.getContractNo()).isEqualTo("C-20260624-001");
		verify(contractMapper).updateById(existing);
		verify(contractBreachClauseMapper).delete(any(LambdaQueryWrapper.class));
		verify(contractBreachClauseMapper, times(3)).insert(any(ContractBreachClause.class));
		verify(contractMapper, never()).insert(any(Contract.class));
	}

	@Test
	@DisplayName("returns null when user has no sleep contract")
	void getMine_noContract_returnsNull() {
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

		assertThat(sleepContractService.getMine(99L)).isNull();
	}

	@Test
	@DisplayName("rejects missing targetBedtime")
	void upsert_missingTargetBedtime_throwsInvalidParameter() {
		validRequest.setTargetBedtime(null);

		assertThatThrownBy(() -> sleepContractService.upsert(1L, validRequest))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	@DisplayName("rejects invalid date range")
	void upsert_invalidDateRange_throwsInvalidParameter() {
		validRequest.setStartDate("2026-07-01");
		validRequest.setEndDate("2026-06-01");

		assertThatThrownBy(() -> sleepContractService.upsert(1L, validRequest))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	@DisplayName("rejects when RECORD clause is not enabled")
	void upsert_recordNotEnabled_throwsInvalidParameter() {
		validRequest.setBreachClauses(List.of(
			clause(SleepContractService.CLAUSE_RECORD, false, null)
		));

		assertThatThrownBy(() -> sleepContractService.upsert(1L, validRequest))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	@DisplayName("generates sequential contract numbers for same day")
	void generateContractNo_sameDay_incrementsSequence() {
		Contract last = new Contract();
		last.setContractNo("C-20260624-002");
		when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(last);

		String contractNo = sleepContractService.generateContractNo(LocalDate.of(2026, 6, 24));

		assertThat(contractNo).isEqualTo("C-20260624-003");
	}

	private static BreachClauseRequest clause(String type, boolean enabled, String contentText) {
		BreachClauseRequest clause = new BreachClauseRequest();
		clause.setType(type);
		clause.setEnabled(enabled);
		clause.setContentText(contentText);
		return clause;
	}

	private static SleepContract sleepContract(Long contractId, LocalTime bedtime) {
		SleepContract sleepContract = new SleepContract();
		sleepContract.setContractId(contractId);
		sleepContract.setTargetBedtime(bedtime);
		return sleepContract;
	}

	private static ContractBreachClause storedClause(String type, boolean enabled, String contentText, int sortOrder) {
		ContractBreachClause clause = new ContractBreachClause();
		clause.setClauseType(type);
		clause.setEnabled(enabled ? 1 : 0);
		clause.setContentText(contentText);
		clause.setSortOrder(sortOrder);
		return clause;
	}
}
