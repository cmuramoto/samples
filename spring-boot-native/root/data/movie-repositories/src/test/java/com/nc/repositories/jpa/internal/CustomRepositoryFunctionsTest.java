package com.nc.repositories.jpa.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.DomainException;
import com.nc.domain.internal.Match;
import com.nc.domain.internal.Movie;
import com.nc.domain.internal.Pair;
import com.nc.domain.internal.QuizzRanking;
import com.nc.repositories.jpa.share.BaseSpringTests;
import com.nc.repositories.jpa.share.TestConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class CustomRepositoryFunctionsTest extends BaseSpringTests {

	static class FailedToFetchActiveRoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;

	}

	static class MultiplePendingMatchesForUserException extends RuntimeException {

		private static final long serialVersionUID = 1L;

	}

	static final class Params {
		String name;
		String nameNonPersisted;
		String password;
		int maxRounds;
		int maxMovies;
	}

	static final long H2_MEM_BAR_MS = 1000;

	static final long H2_FAST_MEM_BAR_MS = 100;

	static final AtomicBoolean PURGED = new AtomicBoolean();

	static void delayForH2(long ms) {
		if (ms > 0) {
			LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ms));
		}
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		var first = new Params();
		first.name = "jonny_the_brave";
		first.password = "super_ultra_secret";
		first.nameNonPersisted = "mary_currie";
		first.maxRounds = 5;
		first.maxMovies = 100;

		return List.of(new Object[][]{ { first } });
	}

	@Parameter(0)
	public Params params;

	@Autowired
	AppUserRepository users;

	@Autowired
	MatchRepository matches;

	@Autowired
	RoundRepository rounds;

	@Autowired
	MovieRepository movies;

	@Autowired
	QuizzRankingRepository quizzes;

	@Test
	public void _01_appUserRepository_willFindByName() {
		Assert.assertEquals(0L, users.count());

		var user = new AppUser(params.name, params.password);

		Assert.assertNull(user.getId());

		var saved = users.save(user);

		Assert.assertSame(user, saved);
		Assert.assertNotNull(user.getId());
		Assert.assertNotNull(users.findById(user.getId()));
		Assert.assertNotSame(user, users.findById(user.getId()));

		Assert.assertNull(users.findByName(params.nameNonPersisted));

		var byName = users.findByName(user.getName());

		Assert.assertNotSame(byName, saved);
		Assert.assertEquals(byName, saved);
	}

	@Test
	public void _02_matchRepository_willLinkWithUser() {
		Assert.assertEquals(0L, matches.count());

		var user = users.findByName(params.name);

		Assert.assertNotNull(user);
		Assert.assertNull(matches.activeFor(user, false));

		var match = Match.startFor(user, params.maxRounds);

		Assert.assertSame(match, matches.save(match));

		Assert.assertNotNull(match.getId());

		var active = matches.activeFor(user, false);

		Assert.assertNotNull(active);

		Assert.assertEquals(match.getId(), active.getId());
		Assert.assertEquals(match.getUser(), active.getUser());
	}

	@Test
	public void _03_0_roundRepository_mainFlow() {
		mainFlow(1);
	}

	@Test
	public void _03_1_round_aggregation_post_flow() {
		var user = users.findByName(params.name);

		Assert.assertNotNull(user);

		var active = matches.activeFor(user, true);

		Assert.assertNotNull(active);

		// we don't finish the last round in _03_roundRepository_mainFlow
		Assert.assertFalse(active.tail().isFinished());

		var answered = rounds.totalRoundsAnswered(user);

		Assert.assertEquals(answered, params.maxRounds - 1);
	}

	@Test
	public void _03_2_will_start_new_match_when_finish() {
		var user = users.findByName(params.name);

		Assert.assertNotNull(user);

		var active = matches.activeFor(user, true);

		Assert.assertNotNull(active);
		Assert.assertFalse(active.isFinished());

		active.finish();

		active = matches.save(active);

		Assert.assertTrue(active.isFinished());

		delayForH2(H2_FAST_MEM_BAR_MS);

		active = matches.save(Match.startFor(user, params.maxRounds));

		Assert.assertFalse(active.isFinished());

		mainFlow(2);
	}

	@Test
	public void _03_3_round_aggregation_post_second_flow() {
		var user = users.findByName(params.name);

		Assert.assertNotNull(user);

		var active = matches.activeFor(user, true);

		Assert.assertNotNull(active);

		// we don't finish the last round in _03_roundRepository_mainFlow
		Assert.assertFalse(active.tail().isFinished());

		var answered = rounds.totalRoundsAnswered(user);

		// when previous match is finished in _03_2_will_start_new_match_when_finish, its tail is
		// finished as well
		Assert.assertEquals(answered, 2 * (params.maxRounds) - 1);
	}

	@Test
	public void _04_movieRepository_willPaginateCorrectly() {
		Assert.assertEquals(2, matches.count());
		Assert.assertEquals(params.maxMovies, movies.count());

		var ids = movies.ids();
		var count = ids.size();
		Assert.assertEquals(params.maxMovies, ids.size());

		var pagesize = 5;

		var totalPages = count / pagesize + ((count % pagesize == 0 ? 0 : 1));

		for (var i = 0; i < totalPages; i++) {

			var page = PageRequest.of(i, pagesize, Sort.by("id").ascending());

			var paged = movies.findAll(page);

			for (var p : paged) {
				ids.removeIf(v -> v.equals(p.getId()));
			}
		}

		Assert.assertEquals(0, ids.size());
	}

	@Test(expected = FailedToFetchActiveRoundException.class)
	public void _05_willCorruptRoundStateWithoutValidation() {
		var user = users.findByName(params.name);
		var match = matches.activeFor(user, true);

		Assert.assertEquals(params.maxRounds, match.roundCount());
		Assert.assertEquals(params.maxRounds - 1, match.rounds().filter(r -> r.isFinished()).count());
		Assert.assertFalse(match.tail().isFinished());

		var ids = movies.ids();
		Assert.assertEquals(params.maxMovies, ids.size());

		var factory = pairFactory();

		var pair = factory.get();

		var first = movies.findById(pair.first()).orElseThrow();
		var second = movies.findById(pair.second()).orElseThrow();

		match.newRound(first, second, false);

		match = matches.save(match);

		Assert.assertEquals(2, rounds.findByMatchAndEndIsNull(match).size());

		try {
			rounds.active(match);
			Assert.fail("Should have thrown");
		} catch (DomainException e) {
			throw new FailedToFetchActiveRoundException();
		}
	}

	@Test(expected = MultiplePendingMatchesForUserException.class)
	public void _06_willCorruptMatchStateWithoutValidation() {
		var user = users.findByName(params.name);
		var match = matches.activeFor(user, true);

		var newMatch = Match.startFor(user, 5);

		matches.save(newMatch);

		Assert.assertNotNull(newMatch.getId());

		Assert.assertNotEquals(match.getId(), newMatch.getId());

		try {
			match = matches.activeFor(user, true);
			Assert.fail("Should have thrown");
		} catch (DomainException e) {
			throw new MultiplePendingMatchesForUserException();
		}
	}

	@Test
	public void _07_willFilterByNotIdWithPagination() {
		var count = movies.count();

		Assert.assertEquals(params.maxMovies, count);

		var first = movies.findAll(PageRequest.of(0, 1, Sort.by("id"))).get().findFirst().orElseThrow();

		var others = movies.findByIdNot(PageRequest.of(0, 1, Sort.by("id")), first.getId());

		Assert.assertEquals(1, others.size());

		var second = others.get(0);

		Assert.assertNotEquals(first.getId(), second.getId());
	}

	@Test
	public void _08_will_integrate_with_security() {

		if (users instanceof UserDetailsService uds) {

			var user = uds.loadUserByUsername("non_existing");

			Assert.assertNull(user);

			user = uds.loadUserByUsername(params.name);

			Assert.assertNotNull(user);
		}
	}

	@Test
	public void _09_quizz_ranking() {

		var user = users.findByName(params.name);

		Assert.assertNotNull(user);

		var quizz = quizzes.findOneByUser(user).orElse(null);

		Assert.assertNull(quizz);

		quizz = new QuizzRanking();
		quizz.setUser(user);
		quizz.setPlayed(1);

		var saved = quizzes.save(quizz);

		Assert.assertNotNull(saved.getId());

		quizz = quizzes.findOneByUser(user).orElse(null);

		Assert.assertNotNull(quizz);

		Assert.assertNotSame(quizz, saved);

		Assert.assertEquals(quizz.getId(), saved.getId());
	}

	@Before
	public void cleanup() {
		if (PURGED.compareAndSet(false, true)) {
			users.deleteAll();
			matches.deleteAll();
			rounds.deleteAll();
			movies.deleteAll();
			// H2 may fail spuriously after a bulk delete and a bulk insert
			delayForH2(H2_MEM_BAR_MS);
			movies.saveAll(mockMovies());
		}
		delayForH2(H2_MEM_BAR_MS);
	}

	private void mainFlow(int expectedCount) {
		Assert.assertEquals(expectedCount, matches.count());
		Assert.assertEquals(params.maxMovies, movies.count());

		var ids = movies.ids();
		Assert.assertEquals(params.maxMovies, ids.size());

		var factory = pairFactory();

		var user = users.findByName(params.name);
		Assert.assertNotNull(user);

		var match = matches.activeFor(user, true);
		Assert.assertNotNull(match);

		var round = rounds.active(match);

		Assert.assertNull(round);

		for (var i = 0; i < params.maxRounds; i++) {
			var pair = factory.get();

			var first = movies.findById(pair.first()).orElseThrow();
			var second = movies.findById(pair.second()).orElseThrow();

			var tail = match.tail();

			if (tail != null) {
				tail.finish();
				rounds.save(tail);
				match = matches.activeFor(user, true);
			}

			round = match.newRound(first, second);

			match = matches.save(match);

			var active = rounds.active(match);

			Assert.assertNotSame(round, active);

			Assert.assertEquals(round.options(), active.options());
			delayForH2(H2_MEM_BAR_MS);
		}
	}

	Movie mock(int seq) {
		var m = new Movie();
		m.setTitle("#A short title" + seq);
		m.setImdbID("imdb#" + seq);
		return m;
	}

	private Iterable<Movie> mockMovies() {
		return () -> IntStream.range(0, params.maxMovies).mapToObj(this::mock).iterator();
	}

	Supplier<Pair> pairFactory() {
		var ids = movies.ids();

		Assert.assertTrue(ids.size() >= 2);

		Assert.assertEquals(params.maxMovies, ids.size());

		return () -> {
			var random = ThreadLocalRandom.current();
			var first = random.nextInt(ids.size());
			var second = random.nextInt(ids.size());
			while (second == first) {
				second = random.nextInt(ids.size());
			}
			return new Pair(first, second);
		};
	}
}
