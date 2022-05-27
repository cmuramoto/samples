package com.nc.domain.frontend.api.v1;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

public class GetOrStartGameResponse {

	@NotNull
	ApiRound current;

	@NotNull
	@PastOrPresent
	LocalDateTime started;

	public ApiRound getCurrent() {
		return current;
	}

	public void setCurrent(ApiRound current) {
		this.current = current;
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public void setStarted(LocalDateTime started) {
		this.started = started;
	}
}
