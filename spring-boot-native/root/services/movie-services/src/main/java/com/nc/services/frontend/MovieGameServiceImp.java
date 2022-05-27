package com.nc.services.frontend;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nc.domain.frontend.api.v1.ApiConstraints;
import com.nc.domain.frontend.api.v1.ApiMatchResult;
import com.nc.domain.frontend.api.v1.ApiMovie;
import com.nc.domain.frontend.api.v1.ApiRound;
import com.nc.domain.frontend.api.v1.ApiUser;
import com.nc.domain.frontend.api.v1.ApiUserRanking;
import com.nc.domain.frontend.api.v1.FinishMatchRequest;
import com.nc.domain.frontend.api.v1.FinishMatchResponse;
import com.nc.domain.frontend.api.v1.FinishRoundRequest;
import com.nc.domain.frontend.api.v1.FinishRoundResponse;
import com.nc.domain.frontend.api.v1.GetOrStartGameRequest;
import com.nc.domain.frontend.api.v1.GetOrStartGameResponse;
import com.nc.domain.frontend.api.v1.TopPlayerRequest;
import com.nc.domain.frontend.api.v1.TopPlayerResponse;
import com.nc.domain.frontend.services.v1.MovieGameService;
import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.AppUserService;
import com.nc.domain.internal.ConstraintService;
import com.nc.domain.internal.Errors;
import com.nc.domain.internal.Match;
import com.nc.domain.internal.MatchService;
import com.nc.domain.internal.MovieService;
import com.nc.domain.internal.QuizzRanking;
import com.nc.domain.internal.Round;

@Component
public class MovieGameServiceImp implements MovieGameService {

	@Autowired
	MatchService matches;

	@Autowired
	AppUserService users;

	@Autowired
	MovieService movies;

	@Autowired
	ConstraintService constraints;

	Match active() {
		return matches.activeFor(playing());
	}

	@Override
	public ApiConstraints changeConstraints(ApiConstraints request) {
		var maxErrors = request.getMaxErrors();
		var maxRounds = request.getMaxRounds();

		if (maxErrors < 1 || maxRounds < 1) {
			throw Errors.get().invalidConstraintValues(maxErrors, maxRounds);
		}

		constraints.setMaxErrors(maxErrors);
		constraints.setMaxRounds(maxRounds);

		return request;
	}

	@Override
	public ApiRound current() {
		var active = active();

		if (active == null) {
			throw Errors.get().userHasNotStartedMatch(playing());
		}

		return toRound(active.tail(), active.roundCount() - 1);
	}

	@Override
	public FinishMatchResponse finish(FinishMatchRequest request) {
		var user = playing();
		var match = matches.finishCurrent(user);

		var response = new FinishMatchResponse();
		response.setResult(toResult(match));

		if (request.isStartNew()) {
			var next = matches.startFor(user);
			response.setNext(toRound(next));
		}

		return response;
	}

	@Override
	public FinishRoundResponse finish(FinishRoundRequest request) {
		var res = new FinishRoundResponse();
		var user = playing();
		var match = matches.finishRound(user, request.getSelected());

		if (match.isFinished()) {
			var result = toResult(match);
			res.setResult(result);
		} else {
			var round = matches.newRound(user);
			res.setNext(toRound(round));
		}

		return res;
	}

	@Override
	public ApiConstraints getConstraints() {
		var rv = new ApiConstraints();
		rv.setMaxErrors(constraints.maxErrors());
		rv.setMaxRounds(constraints.maxRounds());

		return rv;
	}

	@Override
	public ApiUser me() {
		var rv = new ApiUser();
		var user = users.current();
		rv.setName(user.getName());

		var ranking = matches.rankingFor(user);

		rv.setRanking(toApiRanking(user, ranking));

		return rv;
	}

	AppUser playing() {
		return users.current(true);
	}

	@Override
	public GetOrStartGameResponse start(GetOrStartGameRequest request) {
		if (!"movie".equals(request.getCategory())) {
			throw Errors.get().unsupportedCategory(request.getCategory());
		}

		var curr = users.current(true);
		var active = matches.getOrStartFor(curr);

		var response = new GetOrStartGameResponse();

		response.setStarted(active.getStart());
		response.setCurrent(toRound(active));

		return response;
	}

	private ApiUserRanking toApiRanking(AppUser user, QuizzRanking ranking) {
		ApiUserRanking rv;
		if (ranking == null) {
			rv = null;
		} else {
			rv = new ApiUserRanking();
			rv.setUser(user.getName());
			rv.setHits(ranking.getCorrect());
			rv.setPlayed(ranking.getPlayed());
			rv.setScore(ranking.getScore());
		}

		return rv;
	}

	private ApiMovie toMovie(int first) {
		var movie = movies.findById(first);

		var api = new ApiMovie();
		api.setId(movie.getId());
		api.setTitle(movie.getTitle());
		api.setPoster(movie.getPoster());

		return api;
	}

	@Override
	public TopPlayerResponse top(TopPlayerRequest request) {
		if (request.getMax() < 1 || request.getMax() > 100 || request.getPage() < 0) {
			throw Errors.get().invalidTopPlayerRequest(request.getMax(), request.getPage());
		}
		var response = new TopPlayerResponse();

		var top = matches.top(request.getMax(), request.getPage());

		var rankings = top.stream().map(this::toRanking).collect(Collectors.toList());

		response.setRankings(rankings);

		return response;
	}

	ApiUserRanking toRanking(QuizzRanking quizz) {
		var aur = new ApiUserRanking();
		aur.setHits(quizz.getCorrect());
		aur.setPlayed(quizz.getPlayed());
		aur.setScore(quizz.getScore());
		aur.setUser(quizz.getUser().getName());

		return aur;
	}

	private ApiMatchResult toResult(Match match) {
		var result = new ApiMatchResult();

		result.setErrors(match.errorCount());
		result.setHits(match.correctCount());
		result.setStarted(match.getStart());
		result.setFinished(match.getEnd());

		var ranking = matches.rankingFor(match.getUser());

		result.setRanking(toApiRanking(match.getUser(), ranking));

		return result;
	}

	private ApiRound toRound(Match active) {
		return toRound(active.tail(), active.roundCount() - 1);
	}

	ApiRound toRound(Round tail, int tailIndex) {
		if (tail == null) {
			return ApiRound.NO_ROUND;
		}

		var options = tail.options();

		var round = new ApiRound();
		round.setNumber(tailIndex);
		round.setFirst(toMovie(options.first()));
		round.setSecond(toMovie(options.second()));
		round.setSelected(-1);

		return round;
	}
}