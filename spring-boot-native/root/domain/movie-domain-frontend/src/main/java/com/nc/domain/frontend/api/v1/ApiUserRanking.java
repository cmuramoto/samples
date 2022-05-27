package com.nc.domain.frontend.api.v1;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

public class ApiUserRanking {

	@NotBlank
	String user;

	@PositiveOrZero
	int played;

	@PositiveOrZero
	int hits;

	@PositiveOrZero
	double score;

	public int getHits() {
		return hits;
	}

	public int getPlayed() {
		return played;
	}

	public double getScore() {
		return score;
	}

	public String getUser() {
		return user;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
