package com.nc.domain.internal;

public interface ConstraintService {

	int maxErrors();

	int maxRounds();

	void setMaxErrors(int errors);

	void setMaxRounds(int matches);

}
