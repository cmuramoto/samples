package com.nc.repositories.jpa.internal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Match;

public interface MatchRepository extends CrudRepository<Match, Integer> {

	default Match activeFor(AppUser user, boolean inflate) {
		var matches = findByUserAndEndIsNull(user);

		if (matches.size() > 1) {
			throw Errors.get().multipleActiveMatches(user, matches.size());
		}

		var result = matches.isEmpty() ? null : matches.get(0);

		return result != null && inflate ? load(result.getId()) : result;
	}

	long countByUserAndEndIsNotNull(AppUser user);

	List<Match> findByUserAndEndIsNull(AppUser user);

	@Query("select m from Match m LEFT OUTER JOIN FETCH m.rounds r where m.id = ?1")
	Match load(Integer id);
}