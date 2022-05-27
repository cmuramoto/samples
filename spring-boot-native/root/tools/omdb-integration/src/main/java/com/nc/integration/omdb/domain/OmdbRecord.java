package com.nc.integration.omdb.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nc.domain.base.AbstractEntity;

@Entity
public class OmdbRecord extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Title")
	String title;

	@JsonProperty("Year")
	String year;

	@JsonProperty("imdbID")
	String imdbID;

	@JsonProperty("Type")
	String type;

	@JsonProperty("Poster")
	String poster;

	@JsonProperty("Rated")
	String rated;

	@JsonProperty("Released")
	String released;

	@JsonProperty("Runtime")
	String runtime;

	@JsonProperty("Director")
	String director;

	@JsonProperty("Writer")
	@Column(columnDefinition = "TEXT")
	String writer;

	@JsonProperty("Actors")
	String actors;

	@JsonProperty("Plot")
	@Column(columnDefinition = "TEXT")
	String plot;

	@JsonProperty("Language")
	String language;

	@JsonProperty("Country")
	String country;

	@JsonProperty("Awards")
	String awards;

	@JsonProperty("imdbRating")
	String imdbRating;

	@JsonProperty("imdbVotes")
	String imdbVotes;

	@JsonProperty("Ratings")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<OmdbRating> ratings = new ArrayList<>(0);

	@JsonIgnore
	private int page;

	public OmdbRecord copy(OmdbRecord inflated) {
		this.actors = inflated.actors;
		this.awards = inflated.awards;
		this.country = inflated.country;
		this.director = inflated.director;

		this.title = inflated.title;
		this.imdbID = inflated.imdbID;
		this.imdbRating = inflated.imdbRating;
		this.imdbVotes = inflated.imdbVotes;

		this.language = inflated.language;
		this.plot = inflated.plot;
		this.poster = inflated.poster;
		this.rated = inflated.rated;
		this.released = inflated.released;
		this.runtime = inflated.runtime;
		this.type = inflated.type;
		this.writer = inflated.writer;

		if (inflated.ratings != null && !inflated.ratings.isEmpty()) {
			this.ratings.addAll(inflated.ratings);
		}

		return this;
	}

	public String getActors() {
		return actors;
	}

	public String getAwards() {
		return awards;
	}

	public String getCountry() {
		return country;
	}

	public String getDirector() {
		return director;
	}

	public String getImdbID() {
		return imdbID;
	}

	public String getImdbRating() {
		return imdbRating;
	}

	public String getImdbVotes() {
		return imdbVotes;
	}

	public String getLanguage() {
		return language;
	}

	public int getPage() {
		return page;
	}

	public String getPlot() {
		return plot;
	}

	public String getPoster() {
		return poster;
	}

	public String getRated() {
		return rated;
	}

	public List<OmdbRating> getRatings() {
		return ratings;
	}

	public String getReleased() {
		return released;
	}

	public String getRuntime() {
		return runtime;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getWriter() {
		return writer;
	}

	public String getYear() {
		return year;
	}

	public boolean isImdbRecord() {
		return imdbID != null && !imdbID.isBlank();
	}

	public boolean isInflated() {
		return !ratings.isEmpty() || imdbVotes == null || imdbRating == null;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public void setAwards(String awards) {
		this.awards = awards;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	public void setImdbRating(String imdbRating) {
		this.imdbRating = imdbRating;
	}

	public void setImdbVotes(String imdbVotes) {
		this.imdbVotes = imdbVotes;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public void setRated(String rated) {
		this.rated = rated;
	}

	public void setRatings(List<OmdbRating> ratings) {
		this.ratings = ratings;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public void setYear(String year) {
		this.year = year;
	}
}