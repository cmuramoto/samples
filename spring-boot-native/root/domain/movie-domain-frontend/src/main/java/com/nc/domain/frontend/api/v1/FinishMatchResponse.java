package com.nc.domain.frontend.api.v1;

public class FinishMatchResponse {

	ApiRound next;

	ApiMatchResult result;

	public ApiRound getNext() {
		return next;
	}

	public ApiMatchResult getResult() {
		return result;
	}

	public void setNext(ApiRound next) {
		this.next = next;
	}

	public void setResult(ApiMatchResult result) {
		this.result = result;
	}

}
