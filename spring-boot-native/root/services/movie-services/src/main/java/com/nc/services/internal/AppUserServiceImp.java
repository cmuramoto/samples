package com.nc.services.internal;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.AppUserService;
import com.nc.domain.internal.Errors;
import com.nc.repositories.jpa.internal.AppUserRepository;

@Transactional
@Component
public class AppUserServiceImp implements AppUserService, UserDetailsService {

	@Autowired
	AppUserRepository users;

	@Override
	public long count() {
		return users.count();
	}

	@Override
	public AppUser current(boolean failOnNotBound) {
		var ctx = SecurityContextHolder.getContext();
		AppUser rv;

		if (ctx == null) {
			rv = null;
		} else {
			var auth = ctx.getAuthentication();
			String name;

			if (auth == null || (name = auth.getName()) == null || name.isBlank()) {
				rv = null;
			} else {
				rv = users.findByName(name);
			}
		}

		if (rv == null && failOnNotBound) {
			throw Errors.get().userNotBound();
		}

		return rv;
	}

	@Override
	public AppUser findByName(String name) {
		return users.findByName(name);
	}

	@Override
	public UserDetails loadUserByUsername(String name) {
		var user = users.findByName(name);

		if (user != null) {
			return new User(user.getName(), user.getPass(), new ArrayList<>(0));
		}

		return null;
	}
}
