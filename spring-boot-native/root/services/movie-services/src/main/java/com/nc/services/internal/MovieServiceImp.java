package com.nc.services.internal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Movie;
import com.nc.domain.internal.MovieService;
import com.nc.repositories.jpa.internal.MovieRepository;

@Transactional
@Component
public class MovieServiceImp implements MovieService {

	@Autowired
	MovieRepository movies;

	@Override
	public long count() {
		return movies.count();
	}

	@Override
	public Movie findById(int first) {
		return movies.findById(first).orElseThrow(() -> Errors.get().movieNotFound(first));
	}
}