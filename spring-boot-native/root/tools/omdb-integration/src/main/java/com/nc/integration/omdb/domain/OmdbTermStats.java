package com.nc.integration.omdb.domain;

import java.util.stream.IntStream;

import javax.persistence.Entity;

import com.nc.domain.base.AbstractEntity;

@Entity
public class OmdbTermStats extends AbstractEntity implements Cloneable {

	private static final long serialVersionUID = 1L;

	public static final int API_RESULTS_PER_PAGE = 10;

	public static final int MAX_PAGE = 100;

	public String term;

	public int available;

	public int sync;

	int nextPage() {
		if (available == 0) {
			return -1;
		}

		if (sync == 0) {
			return 1;
		}

		return 1 + available / sync + (available % sync == 0 ? 0 : 1);
	}

	public int currentPage() {
		if (available == 0) {
			return -1;
		}

		return 1 + sync / API_RESULTS_PER_PAGE;
	}

	public int totalPages() {
		return available / API_RESULTS_PER_PAGE + ((available % API_RESULTS_PER_PAGE == 0) ? 0 : 1);
	}

	public IntStream pendingPages() {
		var start = currentPage();

		var total = totalPages();

		if (start <= 0 || start > MAX_PAGE || total <= 0 || sync >= available) {
			return IntStream.empty();
		}

		return IntStream.rangeClosed(start, total);
	}

	protected OmdbTermStats clone() {
		return (OmdbTermStats) super.clone();
	}

	public void pageSynchronized(int page) {
		sync = Math.min(page * API_RESULTS_PER_PAGE, available);
	}
}