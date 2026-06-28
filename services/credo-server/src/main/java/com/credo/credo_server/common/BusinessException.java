package com.credo.credo_server.common;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;
	private final HttpStatus httpStatus;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
		this.httpStatus = mapStatus(errorCode);
	}

	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.httpStatus = mapStatus(errorCode);
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	private static HttpStatus mapStatus(ErrorCode errorCode) {
		return switch (errorCode) {
			case INVALID_PARAMETER, WECHAT_AUTH_FAILED -> HttpStatus.BAD_REQUEST;
			case ACCOUNT_DISABLED -> HttpStatus.FORBIDDEN;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}
