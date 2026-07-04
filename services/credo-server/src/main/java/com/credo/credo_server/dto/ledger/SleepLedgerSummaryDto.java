package com.credo.credo_server.dto.ledger;

public class SleepLedgerSummaryDto {

	private int obligationDays;
	private int recordedDays;
	private int pendingDays;
	private int fulfilledDays;
	private int breachDays;

	public int getObligationDays() {
		return obligationDays;
	}

	public void setObligationDays(int obligationDays) {
		this.obligationDays = obligationDays;
	}

	public int getRecordedDays() {
		return recordedDays;
	}

	public void setRecordedDays(int recordedDays) {
		this.recordedDays = recordedDays;
	}

	public int getPendingDays() {
		return pendingDays;
	}

	public void setPendingDays(int pendingDays) {
		this.pendingDays = pendingDays;
	}

	public int getFulfilledDays() {
		return fulfilledDays;
	}

	public void setFulfilledDays(int fulfilledDays) {
		this.fulfilledDays = fulfilledDays;
	}

	public int getBreachDays() {
		return breachDays;
	}

	public void setBreachDays(int breachDays) {
		this.breachDays = breachDays;
	}
}
