package com.nc.services.internal;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.ConstraintService;
import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Match;
import com.nc.domain.internal.MatchService;
import com.nc.domain.internal.MovieSamplingStrategy;
import com.nc.domain.internal.QuizzRanking;
import com.nc.domain.internal.RoundService;
import com.nc.repositories.jpa.internal.AppUserRepository;
import com.nc.repositories.jpa.internal.MatchRepository;
import com.nc.repositories.jpa.internal.QuizzRankingRepository;

@Transactional
@Component
public class MatchServiceImp implements MatchService {

	static Match mustBeActive(AppUser user, Match active) {
		if (active == null) {
			throw Errors.get().userHasNotStartedMatch(user);
		}

		if (active.isFinished()) {
			throw Errors.get().userHasAlreadyFinishedMatch(user, active);
		}

		return active;
	}

	@Autowired
	AppUserRepository users;

	@Autowired
	MatchRepository matches;

	@Autowired
	QuizzRankingRepository quizzes;

	@Autowired
	MovieSamplingStrategy strategy;

	@Autowired
	RoundService rounds;

	@Autowired
	ConstraintService constraints;

	@Override
	public Match activeFor(AppUser user) {
		return matches.activeFor(user, true);
	}

	@Override
	public int countPlayedBy(AppUser user) {
		return (int) matches.countByUserAndEndIsNotNull(user);
	}

	private Match doFinish(Match active, AppUser user) {
		active.finish();
		active = matches.save(active);
		updateStatistics(active, user);
		return active;
	}

	@Override
	public Match finishCurrent(AppUser user) {
		var active = matches.activeFor(user, true);

		if (active != null) {
			return doFinish(active, user);
		}

		throw Errors.get().userHasNotStartedMatch(user);
	}

	@Override
	public Match finishRound(AppUser user, int selected) {
		var active = mustBeActive(user, matches.activeFor(user, false));

		active.finishTail(selected);

		var count = active.errorCount();

		if (count >= constraints.maxErrors() || active.isFinished()) {
			return doFinish(active, user);
		}

		if (active.isFinished()) {
			updateStatistics(active, user);
		}

		return matches.save(active);
	}

	@Override
	public Match getOrStartFor(AppUser user) {
		var match = activeFor(user);

		if (match == null) {
			match = startFor(user);
		}

		return match;
	}

	@Override
	public Match newRound(AppUser user) {
		var active = matches.activeFor(user, false);

		if (active == null) {
			throw Errors.get().userHasNotStartedMatch(user);
		}

		var next = strategy.pickNext(active.seen(), constraints.maxRounds());

		active.newRound(next, true);

		active = matches.save(active);

		return active;
	}

	@Override
	public QuizzRanking rankingFor(AppUser user) {
		return quizzes.findOneByUser(user).orElse(null);
	}

	@Override
	public Match startFor(AppUser user) {
		var active = matches.activeFor(user, false);

		if (active != null) {
			throw Errors.get().userNotFinishedMatch(user, active);
		}

		active = Match.startFor(user, constraints.maxRounds());

		var next = strategy.pickNext(active.seen(), constraints.maxRounds());
		active.newRound(next, true);

		active = matches.save(active);

		return active;
	}

	@Override
	public List<QuizzRanking> top(int max, int page) {
		return quizzes.top(max, page);
	}

	private void updateStatistics(Match active, AppUser user) {
		var quizz = quizzes.findOneByUser(user).orElseGet(() -> {
			var q = new QuizzRanking();
			q.setUser(user);

			return q;
		});

		var played = this.countPlayedBy(user);
		var answers = rounds.countAnsweredBy(user, false);
		var correct = rounds.countAnsweredBy(user, true);

		double pct;

		if (answers > 0) {
			pct = ((double) correct) / answers;
		} else {
			pct = 0;
		}

		quizz.setAnswers(answers);
		quizz.setPlayed(played);
		quizz.setCorrect(correct);
		quizz.setScore(played * pct);

		quizzes.save(quizz);
	}
}