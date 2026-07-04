package com.credo.credo_server.dto.contract;

import java.util.List;

public class SleepContractUpsertRequest {

	private String targetBedtime;
	private String startDate;
	private String endDate;
	private List<BreachClauseRequest> breachClauses;

	public String getTargetBedtime() {
		return targetBedtime;
	}

	public void setTargetBedtime(String targetBedtime) {
		this.targetBedtime = targetBedtime;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<BreachClauseRequest> getBreachClauses() {
		return breachClauses;
	}

	public void setBreachClauses(List<BreachClauseRequest> breachClauses) {
		this.breachClauses = breachClauses;
	}
}
