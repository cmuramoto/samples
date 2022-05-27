package com.nc.movie.security.api;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

public interface UserLookup {
	User lookup(String name);
}
