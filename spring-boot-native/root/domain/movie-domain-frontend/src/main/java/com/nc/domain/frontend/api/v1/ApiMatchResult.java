package com.nc.domain.frontend.api.v1;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class ApiMatchResult {

	@PositiveOrZero
	int hits;

	@PositiveOrZero
	int errors;

	@NotNull
	LocalDateTime started;

	@NotNull
	LocalDateTime finished;

	ApiUserRanking ranking;

	public int getErrors() {
		return errors;
	}

	public LocalDateTime getFinished() {
		return finished;
	}

	public int getHits() {
		return hits;
	}

	public ApiUserRanking getRanking() {
		return ranking;
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public void setFinished(LocalDateTime finished) {
		this.finished = finished;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public void setRanking(ApiUserRanking ranking) {
		this.ranking = ranking;
	}

	public void setStarted(LocalDateTime started) {
		this.started = started;
	}
}
