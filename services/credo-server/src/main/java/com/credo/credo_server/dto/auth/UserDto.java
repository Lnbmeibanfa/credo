package com.credo.credo_server.dto.auth;

public class UserDto {

	private Long id;
	private String phone;
	private String nickname;
	private String avatarUrl;

	public UserDto() {
	}

	public UserDto(Long id, String phone, String nickname, String avatarUrl) {
		this.id = id;
		this.phone = phone;
		this.nickname = nickname;
		this.avatarUrl = avatarUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
