package com.nc.repositories.jpa.internal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nc.domain.internal.AppUser;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {

	AppUser findByName(String name);
}
