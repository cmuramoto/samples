package com.nc.domain.frontend.api.v1;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ApiRound implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final ApiRound NO_ROUND = new ApiRound();

	int number = -1;

	int selected = -1;

	LocalDateTime started;

	ApiMovie first;

	ApiMovie second;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public ApiMovie getFirst() {
		return first;
	}

	public void setFirst(ApiMovie first) {
		this.first = first;
	}

	public ApiMovie getSecond() {
		return second;
	}

	public void setSecond(ApiMovie second) {
		this.second = second;
	}
}
