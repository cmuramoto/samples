package com.nc.domain.internal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.PositiveOrZero;

import com.nc.domain.base.AbstractEntity;

@Entity
public class QuizzRanking extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	AppUser user;

	@PositiveOrZero
	int played;

	@PositiveOrZero
	int answers;

	@PositiveOrZero
	int correct;

	@PositiveOrZero
	double score;

	public int getAnswers() {
		return answers;
	}

	public int getCorrect() {
		return correct;
	}

	public int getPlayed() {
		return played;
	}

	public double getScore() {
		return score;
	}

	public AppUser getUser() {
		return user;
	}

	public void setAnswers(int answers) {
		this.answers = answers;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}
}
