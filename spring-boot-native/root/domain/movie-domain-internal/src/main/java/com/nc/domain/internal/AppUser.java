package com.nc.domain.internal;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.nc.domain.base.AbstractEntity;

@Entity
public class AppUser extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(unique = true)
	@NotBlank
	@Size(min = 4, max = 20)
	String name;

	@Column
	@NotBlank
	@Size(min = 6, max = 200)
	String pass;

	public AppUser() {
		super();
	}

	public AppUser(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppUser other = (AppUser) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(pass, other.pass);
	}

	public String getName() {
		return name;
	}

	public String getPass() {
		return pass;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, pass);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}