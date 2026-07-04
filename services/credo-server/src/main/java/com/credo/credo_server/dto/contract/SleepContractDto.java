package com.credo.credo_server.dto.contract;

import java.util.List;

public class SleepContractDto {

	private Long id;
	private String contractNo;
	private String targetBedtime;
	private String startDate;
	private String endDate;
	private List<BreachClauseDto> breachClauses;
	private String signedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

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

	public List<BreachClauseDto> getBreachClauses() {
		return breachClauses;
	}

	public void setBreachClauses(List<BreachClauseDto> breachClauses) {
		this.breachClauses = breachClauses;
	}

	public String getSignedAt() {
		return signedAt;
	}

	public void setSignedAt(String signedAt) {
		this.signedAt = signedAt;
	}
}
