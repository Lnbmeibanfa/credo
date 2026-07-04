package com.credo.credo_server.dto.ledger;

import java.util.List;

public class SleepDailyViewDto {

	private Long contractId;
	private SleepLedgerSummaryDto summary;
	private List<SleepDailyDayDto> days;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public SleepLedgerSummaryDto getSummary() {
		return summary;
	}

	public void setSummary(SleepLedgerSummaryDto summary) {
		this.summary = summary;
	}

	public List<SleepDailyDayDto> getDays() {
		return days;
	}

	public void setDays(List<SleepDailyDayDto> days) {
		this.days = days;
	}
}
