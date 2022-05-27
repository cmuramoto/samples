package com.nc.domain.frontend.api.v1;

public class ApiUser {

	String name;

	ApiUserRanking ranking;

	public String getName() {
		return name;
	}

	public ApiUserRanking getRanking() {
		return ranking;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRanking(ApiUserRanking ranking) {
		this.ranking = ranking;
	}
}
