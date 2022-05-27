package com.nc.repositories.jpa.internal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Match;
import com.nc.domain.internal.Round;

public interface RoundRepository extends CrudRepository<Round, Integer> {

	default Round active(Match match) {
		var rounds = this.findByMatchAndEndIsNull(match);

		if (rounds.size() > 1) {
			throw Errors.get().multiplePendingRounds(match, rounds.size());
		}

		return rounds.isEmpty() ? null : rounds.get(0);
	}

	long countByMatchAndEndIsNotNullAndIsCorrect(Match match, boolean isCorrect);

	@Query("select count(r) from Round r INNER JOIN r.match m where m.user = ?1 and r.end is not null and r.isCorrect=?2")
	long countRoundsAnswered(AppUser user, boolean correct);

	List<Round> findByMatchAndEndIsNull(Match match);

	@Query("select count(r) from Round r INNER JOIN r.match m where m.user = ?1 and r.end is not null")
	long totalRoundsAnswered(AppUser user);
}