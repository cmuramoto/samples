package com.nc.domain.internal;

import java.util.Set;

public interface MovieSamplingStrategy {

	Pair pickNext(Set<Pair> seen, int minRounds);

}
