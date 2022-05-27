package com.nc.domain.frontend.api.v1;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

public class TopPlayerResponse {

	@NotNull
	LocalDateTime checkedAt;

	@NotNull
	List<ApiUserRanking> rankings;

	public LocalDateTime getCheckedAt() {
		return checkedAt;
	}

	public List<ApiUserRanking> getRankings() {
		return rankings;
	}

	public void setCheckedAt(LocalDateTime checkedAt) {
		this.checkedAt = checkedAt;
	}

	public void setRankings(List<ApiUserRanking> rankings) {
		this.rankings = rankings;
	}

}
