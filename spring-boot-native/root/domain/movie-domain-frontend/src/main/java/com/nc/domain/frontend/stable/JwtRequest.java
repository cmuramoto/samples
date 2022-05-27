package com.nc.domain.frontend.stable;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public final class JwtRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	private String username;
	@NotBlank
	private String password;

	public JwtRequest() {

	}

	public JwtRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}