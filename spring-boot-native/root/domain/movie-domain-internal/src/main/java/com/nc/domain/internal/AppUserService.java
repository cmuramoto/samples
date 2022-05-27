package com.nc.domain.internal;

public interface AppUserService {

	AppUser current(boolean failOnNotBound);

	default AppUser current() {
		return current(true);
	}

	long count();

	AppUser findByName(String name);

}