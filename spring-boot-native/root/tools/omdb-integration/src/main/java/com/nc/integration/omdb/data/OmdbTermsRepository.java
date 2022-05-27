package com.nc.integration.omdb.data;

import java.util.List;
import java.util.Optional;

import com.nc.integration.omdb.domain.OmdbTermStats;
import com.nc.repositories.jpa.internal.AbstractEntityRepository;

public interface OmdbTermsRepository extends AbstractEntityRepository<OmdbTermStats> {

	List<OmdbTermStats> findByTerm(String term);

	default Optional<OmdbTermStats> findOneByTerm(String term) {
		var results = findByTerm(term);
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

}
