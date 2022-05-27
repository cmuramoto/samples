package com.nc.services.internal;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Movie;
import com.nc.domain.internal.MovieSamplingStrategy;
import com.nc.domain.internal.Pair;
import com.nc.repositories.jpa.internal.MovieRepository;

@Transactional
@Component
public class RandomSamplingStrategy implements MovieSamplingStrategy {

	@Autowired
	MovieRepository movies;

	TreeSet<Integer> asFilter(Set<Pair> seen) {
		if (seen == null || seen.isEmpty()) {
			return null;
		}

		return seen.stream().flatMap(s -> s.values().boxed()).collect(Collectors.toCollection(TreeSet::new));
	}

	PageRequest first() {
		return PageRequest.of(0, 1, Sort.by("id").ascending());
	}

	@Override
	public Pair pickNext(Set<Pair> seen, int minRounds) {
		var count = movies.count();

		if (count <= minRounds) {
			throw Errors.get().notEnoughMovies(count, minRounds);
		}

		var filter = asFilter(seen);
		var pair = randomPair(count, filter);

		return pair;
	}

	Movie pickRandom(long count, NavigableSet<Integer> not) {
		var req = random(count, 1);

		if (not == null) {
			var page = movies.findAll(req);
			return page.stream().findFirst().orElse(null);
		} else {
			var filtered = movies.findByIdNotIn(req, not);

			// edge case page + filter overflow
			if (filtered.isEmpty()) {
				filtered = movies.findByIdNotIn(first(), not);

				if (filtered.isEmpty()) {
					filtered = movies.findByIdLessThan(first(), not.first());
				}

				if (filtered.isEmpty()) {
					filtered = movies.findByIdGreaterThan(first(), not.last());
				}

				if (filtered.isEmpty()) {
					throw Errors.get().unableToGenerateSample();
				}
			}

			return filtered.get(0);
		}
	}

	private PageRequest random(long count, int pagesize) {
		var random = ThreadLocalRandom.current();

		var pages = count / pagesize + ((count % pagesize == 0) ? 0 : 1);

		var page = (int) random.nextLong(0, pages);

		return PageRequest.of(page, pagesize, Sort.by("id").ascending());
	}

	Pair randomPair(long count, NavigableSet<Integer> seen) {
		var first = pickRandom(count, seen);

		if (seen == null) {
			seen = new TreeSet<>();
		}
		seen.add(first.getId());

		var second = pickRandom(count, seen);

		return new Pair(first, second);
	}

}
