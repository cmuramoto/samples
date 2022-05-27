package com.nc.domain.frontend.api.v1;

public class TopPlayerRequest {

	int max;

	int page;

	public int getMax() {
		return max;
	}

	public int getPage() {
		return page;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
