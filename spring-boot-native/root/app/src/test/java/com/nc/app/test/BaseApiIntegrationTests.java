package com.nc.app.test;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.LocalServerPort;

import com.nc.domain.frontend.api.v1.ApiConstraints;
import com.nc.domain.frontend.api.v1.ApiMatchResult;
import com.nc.domain.frontend.api.v1.ApiRound;
import com.nc.domain.frontend.api.v1.ApiUser;
import com.nc.domain.frontend.api.v1.FinishMatchRequest;
import com.nc.domain.frontend.api.v1.FinishRoundRequest;
import com.nc.domain.frontend.api.v1.GetOrStartGameRequest;
import com.nc.domain.frontend.api.v1.TopPlayerRequest;
import com.nc.domain.internal.DomainException;
import com.nc.utils.json.JSON;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseApiIntegrationTests {

	static class State {
		ApiUser beforePlay;
		ApiConstraints constraints;
		GameClient client;
		ApiRound current;
		LocalDateTime matchStart;
		ApiMatchResult result;
		Boolean playedAll;
		boolean firstPlayOk;
		boolean secondPlayOk;

		void clearAfterPlay() {
			current = null;
			result = null;
			matchStart = null;
		}

		void reset() {
			beforePlay = null;
			constraints = null;
			client = null;
			current = null;
			matchStart = null;
			result = null;
			firstPlayOk = false;
			secondPlayOk = false;
		}
	}

	static final State STATE = new State();

	static boolean CAN_DELAY = true;

	static void delay(long ms) {
		if (CAN_DELAY) {
			LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ms));
		}
	}

	@LocalServerPort
	protected int port;

	final Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void _000_checkState() {
		Assume.assumeTrue(port > 0);
		Assume.assumeFalse(Modifier.isAbstract(getClass().getModifiers()));

		if (STATE.client == null) {
			STATE.client = new GameClient("mary", "changeme", port);
		}

		if (STATE.constraints == null) {
			STATE.constraints = makeEasyConstraints();
		}

		if (STATE.beforePlay == null) {
			STATE.beforePlay = STATE.client.me();
		}

		delay(100);

		Assume.assumeNotNull(STATE.beforePlay);
		Assume.assumeNotNull(STATE.constraints);
	}

	@Test(expected = DomainException.class)
	public void _01_0_client_should_not_be_able_to_change_invalid_constraints() {
		var constraints = STATE.client.getConstraints();

		log.info("Game Constraints: \n{}\n", JSON.pretty(constraints));

		STATE.client.changeConstraints(new ApiConstraints());

		Assert.fail("Server should not allow maxErros<1 || maxRounds<1");
	}

	@Test(expected = DomainException.class)
	public void _01_1_client_should_not_be_able_to_start_match_for_invalid_category() {
		var req = new GetOrStartGameRequest();
		req.setCategory("series");

		STATE.client.start(new GetOrStartGameRequest());

		Assert.fail("Client should be able to create only matches for movies");
	}

	@Test(expected = DomainException.class)
	public void _02_client_should_not_be_able_fetch_current_round_if_not_match_is_active() {
		attemptFetchWhenInactive();
	}

	@Test(expected = DomainException.class)
	public void _03_client_should_not_be_finish_a_match_if_none_is_active() {
		var req = new FinishMatchRequest();

		var round = STATE.client.current();

		Assert.assertNull(round.getFirst());

		STATE.client.finish(req);

		Assert.fail("Client should be able to finish a match before starting one");
	}

	@Test(expected = DomainException.class)
	public void _04_client_should_not_be_finish_a_round_if_none_is_active() {
		var req = new FinishRoundRequest();

		STATE.client.finish(req);

		Assert.fail("Client should be able to finish a match before starting one");
	}

	@Test
	public void _05_client_should_be_able_to_start_match_with_valid_category() {
		getOrStartMatch();
	}

	@Test
	public void _06_client_should_be_able_to_fetch_current_round_if_has_an_active_match() {
		Assert.assertNotNull(STATE.current);

		var client = STATE.client;

		var response = client.current();

		Assert.assertNotNull(response);

		Assert.assertEquals(STATE.current.getFirst().getId(), response.getFirst().getId());
		Assert.assertEquals(STATE.current.getSecond().getId(), response.getSecond().getId());
	}

	@Test
	public void _07_start_game_should_return_active_match_when_client_has_started_previously() {
		var req = new GetOrStartGameRequest();
		req.setCategory("movie");

		Assert.assertNotNull(STATE.current);
		Assert.assertNotNull(STATE.matchStart);

		var client = STATE.client;

		var response = client.start(req);

		Assert.assertNotNull(response);

		var current = response.getCurrent();

		Assert.assertEquals(current.getFirst().getId(), STATE.current.getFirst().getId());
		Assert.assertEquals(current.getSecond().getId(), STATE.current.getSecond().getId());

		Assert.assertEquals(STATE.matchStart.withNano(0), response.getStarted().withNano(0));
	}

	@Test(expected = DomainException.class)
	public void _08_client_should_not_be_able_to_answer_with_invalid_movie_id() {
		Assert.assertNotNull(STATE.current);
		Assert.assertNotNull(STATE.matchStart);

		var req = new FinishRoundRequest();
		req.setSelected(-1);

		var client = STATE.client;

		client.finish(req);

		Assert.fail("Client should not have been able to send invalid id");
	}

	@Test
	public void _09_client_should_be_able_to_finish_round_with_valid_id() {
		playOneRound();
	}

	@Test
	public void _10_0_client_should_be_able_to_keep_playing_until_max_errors_or_max_rounds() {
		playUntilTheEnd();
		STATE.clearAfterPlay();
		STATE.firstPlayOk = true;
	}

	@Test
	public void _10_1_check_my_ranking() {
		Assume.assumeTrue(STATE.firstPlayOk);
		checkRanking(1);
	}

	@Test(expected = DomainException.class)
	public void _11_expect_no_active_match_after_client_finished_playing() {
		attemptFetchWhenInactive();
	}

	@Test
	public void _12_client_should_be_able_to_start_new_match_after_finished_playing() {
		getOrStartMatch();
	}

	@Test
	public void _13_0_alternate_flow() {
		Assert.assertNotNull(STATE.current);
		Assert.assertNotNull(STATE.playedAll);

		var previous = STATE.playedAll;

		if (previous) {
			STATE.constraints = makeDificultConstraints();
		} else {
			STATE.constraints = makeEasyConstraints();
		}

		STATE.playedAll = null;

		delay(1000);
		playOneRound();
		playUntilTheEnd();

		Assert.assertEquals(previous, !STATE.playedAll);

		STATE.clearAfterPlay();
		STATE.secondPlayOk = true;
	}

	@Test
	public void _13_1_check_my_ranking() {
		Assume.assumeTrue(STATE.secondPlayOk);
		checkRanking(2);
	}

	@Test
	public void _13_2_check_top_rankings() {
		var name = STATE.beforePlay.getName();

		Assume.assumeNotNull(name);

		var req = new TopPlayerRequest();
		req.setMax(100);
		req.setPage(0);

		var top = STATE.client.top(req);

		log.info("Top players: \n{}\n", JSON.pretty(top));

		Assert.assertNotNull(top);
		Assert.assertNotNull(top.getRankings());

		log.info("{} should be in top ranking", name);

		var any = top.getRankings().stream().anyMatch(r -> r.getUser().equals(STATE.beforePlay.getName()));

		Assert.assertTrue(any);
	}

	@Test(expected = DomainException.class)
	public void _13_3_client_should_not_be_able_to_check_top_rankings_with_invalid_params() {
		var req = new TopPlayerRequest();
		req.setMax(-1);
		req.setPage(-1);

		STATE.client.top(req);

		Assert.fail("Should not reach here");
	}

	@Test
	public void _14_user_can_start_match_and_finish_before_playing_all_rounds() {
		Assume.assumeTrue(STATE.secondPlayOk);
		Assert.assertNull(STATE.current);
		var client = STATE.client;

		var req = new GetOrStartGameRequest();
		req.setCategory("movie");
		var res = client.start(req);

		var current = res.getCurrent();
		Assert.assertNotNull(current);

		var finish = new FinishMatchRequest();
		finish.setStartNew(true);

		delay(100);
		var next = client.finish(finish);

		Assert.assertNotNull(next.getNext());

		checkRanking(3);
	}

	@Test
	public void _15_user_can_access_api_docs() {
		var client = STATE.client;

		var docs = client.apiDocs();
		Assert.assertNotNull(docs);

		var schema = JSON.parse(docs);

		Assert.assertFalse(schema.isEmpty());
	}

	@Test
	public void _16_user_can_access_swagger_ui() {
		var client = STATE.client;

		var index = client.swaggerUI(null);
		Assert.assertNotNull(index);
		Assert.assertTrue(index.contains("<!DOCTYPE html>"));

		index = client.swaggerUI("#/jwt-authentication-controller/createAuthenticationToken");
		Assert.assertNotNull(index);
		Assert.assertTrue(index.contains("<!DOCTYPE html>"));

		index = client.swaggerUI("#/game-controller/ranking");
		Assert.assertNotNull(index);
		Assert.assertTrue(index.contains("<!DOCTYPE html>"));
	}

	private void attemptFetchWhenInactive() {
		STATE.client.current();

		Assert.fail("Client should be able to create only matches for movies");
	}

	private ApiConstraints changeGameConstraints(int maxErrors, int maxRounds) {
		var constraints = new ApiConstraints();
		constraints.setMaxErrors(maxErrors);
		constraints.setMaxRounds(maxRounds);

		var result = STATE.client.changeConstraints(constraints);

		return result;
	}

	private void checkRanking(int numMatchesPlayed) {
		var me = STATE.client.me();

		log.info("Client ranking after {} matches: \n{}\n", numMatchesPlayed, JSON.pretty(me));

		Assert.assertEquals(STATE.beforePlay.getName(), me.getName());

		var ranking = me.getRanking();

		Assert.assertNotNull(ranking);
		Assert.assertEquals(numMatchesPlayed, ranking.getPlayed());

	}

	private void getOrStartMatch() {
		var req = new GetOrStartGameRequest();
		req.setCategory("movie");

		Assert.assertNull(STATE.current);

		var client = STATE.client;

		var response = client.start(req);

		Assert.assertNotNull(response.getCurrent());
		Assert.assertNotNull(response.getStarted());

		STATE.current = response.getCurrent();
		STATE.matchStart = response.getStarted();
	}

	private ApiConstraints makeDificultConstraints() {
		return changeGameConstraints(3, 100);
	}

	private ApiConstraints makeEasyConstraints() {
		return changeGameConstraints(Integer.MAX_VALUE, 10);
	}

	private void playOneRound() {
		Assert.assertNotNull(STATE.current);
		Assert.assertNotNull(STATE.matchStart);

		var req = new FinishRoundRequest();
		req.setSelected(STATE.current.getFirst().getId());

		var client = STATE.client;

		var response = client.finish(req);

		var next = response.getNext();
		Assert.assertNotNull(next);
		Assert.assertNotEquals(STATE.current.getFirst().getId(), next.getFirst().getId());
		Assert.assertNotEquals(STATE.current.getSecond().getId(), next.getSecond().getId());

		STATE.current = next;
	}

	private void playUntilTheEnd() {
		Assert.assertNotNull(STATE.current);
		Assert.assertNotNull(STATE.matchStart);

		var played = 1;
		// first round was played in previous test
		var constraints = STATE.constraints;
		for (var i = 1; i < constraints.getMaxRounds(); i++) {
			var req = new FinishRoundRequest();
			req.setSelected(STATE.current.getFirst().getId());

			var client = STATE.client;

			delay(100);
			var response = client.finish(req);

			played++;

			var next = response.getNext();

			if (next == null) {
				var result = response.getResult();

				Assert.assertNotNull(result);

				if (played < constraints.getMaxRounds()) {
					log.info("User played match was prematurelly ended because he made too many mistakes!\nConstraints: {}\n Result: \n{}\n", //
							JSON.pretty(constraints), JSON.pretty(result));
					Assert.assertTrue(result.getErrors() <= constraints.getMaxErrors());

					STATE.result = result;
					STATE.playedAll = false;
					break;
				} else {
					log.info("User played match until the end!\nConstraints: {}\n Result: \n{}\n", //
							JSON.pretty(constraints), JSON.pretty(result));
					Assert.assertTrue(result.getErrors() <= constraints.getMaxErrors());

					Assert.assertEquals(constraints.getMaxRounds(), result.getHits() + result.getErrors());
					STATE.result = result;
					STATE.playedAll = true;
				}

				Assert.assertTrue(result.getErrors() + result.getHits() <= constraints.getMaxRounds());
			} else {
				STATE.current = next;
			}
		}

		Assert.assertNotNull(STATE.result);
	}

}
