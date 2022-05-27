package com.nc.integration.omdb.data;

import java.util.List;
import java.util.Optional;

import com.nc.integration.omdb.domain.OmdbRecord;
import com.nc.repositories.jpa.internal.AbstractEntityRepository;

public interface OmdbRecordRepository extends AbstractEntityRepository<OmdbRecord> {

	List<OmdbRecord> findByImdbID(String imdbId);

	default Optional<OmdbRecord> findOneByImdbID(String imdbId) {
		var results = this.findByImdbID(imdbId);
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}
}
