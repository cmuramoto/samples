package com.nc.repositories.jpa.internal;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.nc.domain.internal.Movie;

public interface MovieRepository extends AbstractEntityRepository<Movie> {

	List<Movie> findByIdGreaterThan(Pageable page, Integer id);

	List<Movie> findByIdLessThan(Pageable page, Integer id);

	List<Movie> findByIdNot(Pageable page, Integer id);

	List<Movie> findByIdNotIn(Pageable page, Set<Integer> ids);
}
