package com.credo.credo_server.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneLoginResponse {

	private String token;
	private UserDto user;
	private boolean isNewUser;

	public PhoneLoginResponse() {
	}

	public PhoneLoginResponse(String token, UserDto user, boolean isNewUser) {
		this.token = token;
		this.user = user;
		this.isNewUser = isNewUser;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	@JsonProperty("isNewUser")
	public boolean isNewUser() {
		return isNewUser;
	}

	@JsonProperty("isNewUser")
	public void setNewUser(boolean newUser) {
		isNewUser = newUser;
	}
}
