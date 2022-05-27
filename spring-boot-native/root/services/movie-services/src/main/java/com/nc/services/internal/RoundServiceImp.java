package com.nc.services.internal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.RoundService;
import com.nc.repositories.jpa.internal.RoundRepository;

@Component
@Transactional
public class RoundServiceImp implements RoundService {

	@Autowired
	RoundRepository rounds;

	@Override
	public int countAnsweredBy(AppUser user, boolean onlyCorrect) {
		int rv;
		if (onlyCorrect) {
			rv = (int) rounds.countRoundsAnswered(user, true);
		} else {
			rv = (int) rounds.totalRoundsAnswered(user);
		}

		return rv;
	}

}
