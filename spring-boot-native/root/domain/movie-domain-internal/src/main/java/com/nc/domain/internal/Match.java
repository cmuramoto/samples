package com.nc.domain.internal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.nc.domain.base.AbstractEntity;

@Entity
public class Match extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public static Match startFor(AppUser user, int maxRounds) {
		var m = new Match();
		m.user = user;
		m.maxRounds = maxRounds;

		m.start = LocalDateTime.now();

		return m;
	}

	@ManyToOne
	private AppUser user;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "match")
	@OrderBy("start")
	private List<Round> rounds = new ArrayList<>(0);

	private LocalDateTime start;

	private LocalDateTime end;

	private int maxRounds;

	boolean add(Round e, boolean validate) {
		if (validate) {
			if (rounds.size() >= maxRounds) {
				throw Errors.get().maxRoundsReached(maxRounds);
			}

			var tail = tail();

			if (tail != null && !tail.isFinished()) {
				throw Errors.get().pendingTail(this, tail);
			}
		}
		return rounds.add(e);
	}

	public int correctCount() {
		var rounds = this.rounds;
		if (rounds == null || rounds.isEmpty()) {
			return 0;
		}
		return (int) rounds.stream().filter(r -> r.isFinished() && r.isCorrect()).count();
	}

	public int errorCount() {
		var rounds = this.rounds;
		if (rounds == null || rounds.isEmpty()) {
			return 0;
		}
		return (int) rounds.stream().filter(r -> r.isFinished() && !r.isCorrect()).count();
	}

	public void finish() {
		var tail = tail();
		if (tail != null) {
			tail.finish();
		}
		if (this.end == null) {
			this.end = LocalDateTime.now();
		}
	}

	public int finishedRoundCount() {
		if (rounds == null || rounds.isEmpty()) {
			return 0;
		}
		return (int) rounds.stream().filter(r -> r.isFinished()).count();
	}

	public void finishTail(int selected) {
		if (isFinished()) {
			throw Errors.get().matchAlreadyFinished();
		}

		var tail = tail();

		if (tail != null) {
			tail.finish(selected);
		} else {
			throw Errors.get().cantFinishRoundNotStarted();
		}

		if (this.rounds != null && this.rounds.size() == maxRounds && !isFinished()) {
			this.end = LocalDateTime.now();
		}
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public int getMaxRounds() {
		return maxRounds;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public AppUser getUser() {
		return user;
	}

	public boolean isFinished() {
		return this.end != null;
	}

	public Round newRound(Movie first, Movie second) {
		return newRound(first, second, true);
	}

	public Round newRound(Movie first, Movie second, boolean validate) {
		if (validate) {
			var tail = tail();

			if (tail != null && !tail.isFinished()) {
				throw Errors.get().pendingTail(this, tail);
			}

			if (first.getId().equals(second.getId())) {
				throw Errors.get().invalidPair(first);
			}
		}

		return newRound(new Pair(first, second), validate);
	}

	public Round newRound(Pair pair, boolean validate) {
		var seen = seen();

		if (seen.contains(pair)) {
			throw Errors.get().seenHasPair(this, pair);
		}

		var round = new Round();
		round.match = this;
		round.start = LocalDateTime.now();
		round.options = pair;
		this.add(round, validate);

		return round;
	}

	public int roundCount() {
		return rounds.size();
	}

	public Stream<Round> rounds() {
		return this.rounds == null ? Stream.empty() : this.rounds.stream();
	}

	public Set<Pair> seen() {
		return rounds().map(r -> r.options).collect(Collectors.toCollection(TreeSet::new));
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public void setMaxRounds(int maxRounds) {
		this.maxRounds = maxRounds;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public Round tail() {
		return rounds.isEmpty() ? null : rounds.get(rounds.size() - 1);
	}
}