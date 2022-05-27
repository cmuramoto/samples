package com.nc.app.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(PersistenceConfig.class)
@EnableTransactionManagement
@ComponentScan({ "com.nc.services.internal", "com.nc.services.frontend" })
public class ServicesConfig {

	@Autowired
	EntityManagerFactory factory;

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(factory);
	}

}
