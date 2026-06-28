package com.credo.credo_server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secret;
	private int expireHours;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getExpireHours() {
		return expireHours;
	}

	public void setExpireHours(int expireHours) {
		this.expireHours = expireHours;
	}
}
