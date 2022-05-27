package com.nc.domain.internal;

import java.util.Objects;
import java.util.stream.IntStream;

import javax.persistence.Embeddable;

@Embeddable
public final class Pair implements Comparable<Pair> {

	private int first;

	private int second;

	private int correct;

	Pair() {

	}

	public Pair(int first, int second) {
		if (Integer.compare(first, second) > 0) {
			var tmp = first;
			first = second;
			second = tmp;
		}
		this.first = first;
		this.second = second;
	}

	public Pair(Movie first, Movie second) {
		this(first.getId(), second.getId());

		this.correct = first.compareTo(second) >= 0 ? first.getId() : second.getId();
	}

	@Override
	public int compareTo(Pair o) {
		var cmp = Integer.compare(first, o.first);

		if (cmp == 0) {
			cmp = Integer.compare(second, o.second);
		}

		return cmp;
	}

	public boolean contains(int selected) {
		return first == selected || second == selected;
	}

	public int correct() {
		return this.correct;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		return first == other.first && second == other.second;
	}

	public int first() {
		return this.first;
	}

	public int getCorrect() {
		return correct;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	public int second() {
		return this.second;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public IntStream values() {
		return IntStream.of(first, second);
	}
}
