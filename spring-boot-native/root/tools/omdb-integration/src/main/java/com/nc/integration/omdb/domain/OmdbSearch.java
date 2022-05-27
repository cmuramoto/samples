package com.nc.integration.omdb.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nc.utils.json.JSON;

public class OmdbSearch {
	@JsonProperty("Search")
	public List<OmdbRecord> records;

	@JsonProperty("totalResults")
	public int available;

	@JsonProperty("Response")
	boolean results;

	public String toString() {
		return JSON.pretty(this);
	}

	public boolean isEmpty() {
		return !results || records == null || records.isEmpty();
	}
}
