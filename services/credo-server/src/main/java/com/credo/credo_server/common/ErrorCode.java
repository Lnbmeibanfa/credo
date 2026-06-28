package com.credo.credo_server.common;

public enum ErrorCode {

	SUCCESS("SUCCESS", "OK"),
	INVALID_PARAMETER("INVALID_PARAMETER", "Invalid request parameters"),
	WECHAT_AUTH_FAILED("WECHAT_AUTH_FAILED", "WeChat authorization failed"),
	ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Account is disabled"),
	INTERNAL_ERROR("INTERNAL_ERROR", "Internal server error");

	private final String code;
	private final String defaultMessage;

	ErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	public String getCode() {
		return code;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}
}
