package com.nc.domain.frontend.stable;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public final class JwtResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	private String jwtToken;

	public JwtResponse() {
	}

	public JwtResponse(String jwttoken) {
		this.jwtToken = jwttoken;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

}