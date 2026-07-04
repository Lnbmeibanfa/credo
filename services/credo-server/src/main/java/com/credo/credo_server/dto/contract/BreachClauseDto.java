package com.credo.credo_server.dto.contract;

public class BreachClauseDto {

	private String type;
	private boolean enabled;
	private String contentText;

	public BreachClauseDto() {
	}

	public BreachClauseDto(String type, boolean enabled, String contentText) {
		this.type = type;
		this.enabled = enabled;
		this.contentText = contentText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}
}
