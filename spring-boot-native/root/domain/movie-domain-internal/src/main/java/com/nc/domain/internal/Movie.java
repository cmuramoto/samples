package com.nc.domain.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import com.nc.domain.base.AbstractEntity;

@Entity
public class Movie extends AbstractEntity implements Comparable<Movie> {

	private static final long serialVersionUID = 1L;

	@NotBlank
	String title;

	@NotBlank
	String imdbID;

	@Column(columnDefinition = "TEXT")
	String poster;

	@PositiveOrZero
	float score;

	@PositiveOrZero
	int totalVotes;

	@Override
	public int compareTo(Movie o) {
		return Double.compare(this.composedScore(), o.composedScore());
	}

	public double composedScore() {
		return this.score * this.totalVotes;
	}

	public String getImdbID() {
		return imdbID;
	}

	public String getPoster() {
		return poster;
	}

	public float getScore() {
		return score;
	}

	public String getTitle() {
		return title;
	}

	public int getTotalVotes() {
		return totalVotes;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}
}
