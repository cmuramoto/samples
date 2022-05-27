package com.nc.domain.internal;

import java.util.List;

public interface MatchService {

	Match activeFor(AppUser user);

	int countPlayedBy(AppUser user);

	Match finishCurrent(AppUser user);

	Match finishRound(AppUser user, int selected);

	Match getOrStartFor(AppUser user);

	Match newRound(AppUser user);

	QuizzRanking rankingFor(AppUser user);

	Match startFor(AppUser user);

	List<QuizzRanking> top(int max, int page);
}