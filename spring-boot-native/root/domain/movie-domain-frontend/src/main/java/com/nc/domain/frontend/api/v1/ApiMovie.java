package com.nc.domain.frontend.api.v1;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotBlank;

public class ApiMovie implements Serializable {

	private static final long serialVersionUID = 1L;

	int id;

	@NotBlank
	String title;

	String poster;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiMovie other = (ApiMovie) obj;
		return id == other.id;
	}

	public int getId() {
		return id;
	}

	public String getPoster() {
		return poster;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
