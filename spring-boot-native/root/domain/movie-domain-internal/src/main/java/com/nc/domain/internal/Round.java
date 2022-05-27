package com.nc.domain.internal;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.nc.domain.base.AbstractEntity;

@Entity
public class Round extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Embedded
	Pair options;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	Match match;

	boolean isCorrect;

	LocalDateTime start;

	LocalDateTime end;

	public void finish() {
		if (end == null) {
			this.end = LocalDateTime.now();
		}
	}

	public void finish(int selected) {
		if (isFinished()) {
			throw Errors.get().roundAlreadyFinished();
		}

		if (!options.contains(selected)) {
			throw Errors.get().roundFinishingWithInvalidSelection(selected);
		}

		this.isCorrect = options.getCorrect() == selected;

		finish();
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public Match getMatch() {
		return match;
	}

	public Pair getOptions() {
		return options;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getStarted() {
		return start;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public boolean isFinished() {
		return end != null;
	}

	public Pair options() {
		return this.options;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public void setOptions(Pair options) {
		this.options = options;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public Round with(Movie first, Movie second) {
		this.options = new Pair(first, second);
		return this;
	}
}