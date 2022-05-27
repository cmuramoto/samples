package com.nc.integration.omdb.domain;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nc.domain.base.AbstractEntity;

@Entity
public class OmdbRating extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Source")
	String source;

	@JsonProperty("Value")
	String value;

	public boolean isImdb() {
		return "internet movie database".equalsIgnoreCase(source);
	}

	public String getSource() {
		return source;
	}

	public String getValue() {
		return value;
	}

}
