package com.nc.services.integration;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

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
import org.springframework.test.context.ContextConfiguration;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.ConstraintService;
import com.nc.domain.internal.DomainException;
import com.nc.domain.internal.MatchService;
import com.nc.repositories.jpa.internal.AppUserRepository;
import com.nc.repositories.jpa.internal.MovieRepository;
import com.nc.services.share.BaseSpringTests;
import com.nc.services.share.IntegrationTestConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { IntegrationTestConfig.class })
public class GameFlowTest extends BaseSpringTests {

	static class Params {
		int maxMovies;
		String user;
	}

	static final AtomicBoolean PURGED = new AtomicBoolean();

	static final long H2_MEM_BAR_MS = 100;

	static void delayForH2() {
		delayForH2(H2_MEM_BAR_MS);
	}

	static void delayForH2(long ms) {
		LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ms));
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		var params = new Params();
		params.maxMovies = 100;
		params.user = "john";
		return List.of(new Object[][]{ { params } });
	}

	@Autowired
	AppUserRepository users;

	@Autowired
	MovieRepository movies;

	@Autowired
	MatchService service;

	@Autowired
	ConstraintService constraints;

	@Parameter(0)
	public Params params;

	@Test
	public void _01_will_start_if_user_has_no_pending_matches() {
		var user = active();

		Assert.assertNull(service.activeFor(user));

		var match = service.startFor(user);

		Assert.assertNotNull(match);
	}

	@Test(expected = DomainException.class)
	public void _02_will_fail_to_start_if_user_has_pending_matches() {
		var user = active();

		Assert.assertNotNull(service.activeFor(user));

		service.startFor(user);

		Assert.fail();
	}

	@Test(expected = DomainException.class)
	public void _03_will_fail_to_start_next_round_if_user_hasnt_finished_previous_round() {
		var user = active();

		Assert.assertNotNull(service.activeFor(user));

		service.newRound(user);

		Assert.fail();
	}

	@Test(expected = DomainException.class)
	public void _04_will_fail_to_finish_round_if_id_is_not_in_tail() {
		var user = active();

		Assert.assertNotNull(service.activeFor(user));

		service.finishRound(user, -1);

		Assert.fail();
	}

	@Test
	public void _04_will_successfully_finish_round_if_id_is_in_tail_and_round_is_not_finished() {
		var user = active();

		var active = service.activeFor(user);
		Assert.assertNotNull(active);

		var tail = active.tail();

		Assert.assertNotNull(tail);

		active = service.finishRound(user, tail.options().correct());

		Assert.assertTrue(active.tail().isFinished());
	}

	@Test
	public void _05_will_successfully_start_new_round_if_tail_is_finished_and_rounds_is_less_than_maxRounds() {
		var user = active();

		var active = service.activeFor(user);
		Assert.assertNotNull(active);

		var tail = active.tail();

		Assert.assertNotNull(tail);
		Assert.assertTrue(tail.isFinished());

		var count = active.roundCount();
		Assert.assertTrue(count < constraints.maxRounds());

		active = service.newRound(user);

		Assert.assertEquals(count + 1, active.roundCount());
	}

	@Test
	public void _05_will_successfully_start_remaining_rounds_while_round_count_is_less_than_maxRounds() {
		var user = active();

		var active = service.activeFor(user);
		Assert.assertNotNull(active);

		var current = active.roundCount();

		var max = constraints.maxRounds();
		Assert.assertTrue(current < max);

		while (current < max) {
			var tail = active.tail();

			Assert.assertNotNull(tail);

			delayForH2();

			service.finishRound(user, tail.options().correct());

			delayForH2();

			active = service.newRound(user);

			var next = active.roundCount();

			Assert.assertEquals(current + 1, next);
			current = next;
		}

	}

	@Test(expected = DomainException.class)
	public void _06_will_fail_to_start_new_round_if_round_count_reached_than_maxRounds() {
		var user = active();
		var active = service.activeFor(user);

		Assert.assertNotNull(active);

		Assert.assertEquals(constraints.maxRounds(), active.roundCount());

		Assert.assertFalse(active.tail().isFinished());

		active = service.finishRound(user, active.tail().options().correct());

		Assert.assertTrue(active.tail().isFinished());

		Assert.assertEquals(constraints.maxRounds(), active.roundCount());

		service.newRound(user);

		Assert.fail();
	}

	AppUser active() {
		return users.findByName(params.user);
	}

	@Before
	public void checkHasActiveUser() {
		Assert.assertTrue(constraints.maxRounds() > 2);
		Assert.assertNotNull(active());
		Assert.assertTrue(movies.count() > 100);
		delayForH2(1000);
	}

}