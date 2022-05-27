package com.nc.repositories.jpa.internal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface AbstractEntityRepository<T> extends PagingAndSortingRepository<T, Integer> {

	@Query("select p.id from #{#entityName} p")
	List<Integer> ids();
}
