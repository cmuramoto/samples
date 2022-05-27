package com.nc.repositories.jpa.internal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.QuizzRanking;

public interface QuizzRankingRepository extends AbstractEntityRepository<QuizzRanking> {

	Optional<QuizzRanking> findOneByUser(AppUser user);

	default List<QuizzRanking> top(int max, int page) {
		var req = PageRequest.of(page, max, Sort.by("score").descending());

		return this.findAll(req).toList();
	}

}
