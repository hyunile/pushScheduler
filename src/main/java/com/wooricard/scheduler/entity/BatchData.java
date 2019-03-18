package com.wooricard.scheduler.entity;

public class BatchData {
	private String batchId;
	private String status;
	
	public BatchData(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchId() {
		return batchId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
