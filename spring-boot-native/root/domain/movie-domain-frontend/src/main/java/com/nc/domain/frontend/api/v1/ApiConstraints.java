package com.nc.domain.frontend.api.v1;

import javax.validation.constraints.Positive;

public class ApiConstraints {

	@Positive
	int maxRounds;

	@Positive
	int maxErrors;

	public int getMaxErrors() {
		return maxErrors;
	}

	public int getMaxRounds() {
		return maxRounds;
	}

	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	public void setMaxRounds(int maxRounds) {
		this.maxRounds = maxRounds;
	}

}
