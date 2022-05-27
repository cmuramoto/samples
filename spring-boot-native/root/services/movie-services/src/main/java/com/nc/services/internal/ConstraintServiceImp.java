package com.nc.services.internal;

import java.lang.invoke.VarHandle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.ConstraintService;

@Component
public class ConstraintServiceImp implements ConstraintService {

	@Value("#{systemProperties['app.match.maxRounds'] ?: '10'}")
	volatile int maxRounds;

	@Value("#{systemProperties['app.match.maxErrors'] ?: '3'}")
	volatile int maxErrors;

	@Override
	public int maxErrors() {
		return maxErrors;
	}

	@Override
	public int maxRounds() {
		return maxRounds;
	}

	@Override
	public void setMaxErrors(int errors) {
		this.maxErrors = errors;
		VarHandle.fullFence();
	}

	@Override
	public void setMaxRounds(int rounds) {
		this.maxRounds = rounds;
		VarHandle.fullFence();
	}

}
