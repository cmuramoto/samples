package com.nc.domain.base;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.PositiveOrZero;

import com.nc.utils.json.JSON;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@PositiveOrZero
	protected Integer id;

	public Integer getId() {
		return id;
	}

	public String toString() {
		return Integer.toString(id);
	}

	protected AbstractEntity clone() {
		try {
			var clone = (AbstractEntity) super.clone();
			clone.id = null;
			return clone;
		} catch (Exception e) {
			throw new InternalError(e);
		}
	}

	public String toPrettyJson() {
		return JSON.pretty(this);
	}
}
