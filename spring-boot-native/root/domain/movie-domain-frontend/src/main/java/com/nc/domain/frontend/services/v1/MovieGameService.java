package com.nc.domain.frontend.services.v1;

import com.nc.domain.frontend.api.v1.ApiConstraints;
import com.nc.domain.frontend.api.v1.ApiRound;
import com.nc.domain.frontend.api.v1.ApiUser;
import com.nc.domain.frontend.api.v1.FinishMatchRequest;
import com.nc.domain.frontend.api.v1.FinishMatchResponse;
import com.nc.domain.frontend.api.v1.FinishRoundRequest;
import com.nc.domain.frontend.api.v1.FinishRoundResponse;
import com.nc.domain.frontend.api.v1.GetOrStartGameRequest;
import com.nc.domain.frontend.api.v1.GetOrStartGameResponse;
import com.nc.domain.frontend.api.v1.TopPlayerRequest;
import com.nc.domain.frontend.api.v1.TopPlayerResponse;

public interface MovieGameService {
	ApiConstraints changeConstraints(ApiConstraints request);

	ApiRound current();

	FinishMatchResponse finish(FinishMatchRequest request);

	FinishRoundResponse finish(FinishRoundRequest request);

	ApiConstraints getConstraints();

	ApiUser me();

	GetOrStartGameResponse start(GetOrStartGameRequest request);

	TopPlayerResponse top(TopPlayerRequest request);
}