package com.credo.credo_server.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalTime;

@TableName("sleep_contract")
public class SleepContract {

	@TableId
	private Long contractId;
	private LocalTime targetBedtime;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public LocalTime getTargetBedtime() {
		return targetBedtime;
	}

	public void setTargetBedtime(LocalTime targetBedtime) {
		this.targetBedtime = targetBedtime;
	}
}
