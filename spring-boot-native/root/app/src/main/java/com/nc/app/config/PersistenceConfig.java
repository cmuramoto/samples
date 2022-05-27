package com.nc.app.config;

import java.util.HashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.nc.domain.base.AbstractEntity;
import com.nc.domain.internal.AppUser;
import com.nc.domain.internal.Match;
import com.nc.domain.internal.Movie;
import com.nc.domain.internal.QuizzRanking;
import com.nc.domain.internal.Round;
import com.nc.utils.json.JSON;

@Configuration
// @EntityScan("com.nc.domain.internal")
@EnableJpaRepositories(basePackages = { "com.nc.repositories.jpa" })
public class PersistenceConfig {

	final Logger log;

	@Autowired
	DataSource dataSource;

	public PersistenceConfig() {
		log = LoggerFactory.getLogger(getClass());
		log.info("Instantiated Persistence config");
	}

	EntityManagerFactory byBuilder() {
		log.info("byBuilder");

		var props = new HashMap<String, String>();
		props.put(AvailableSettings.DIALECT, AppEnv.hibernateDialect());
		props.put(AvailableSettings.SHOW_SQL, AppEnv.hibernateShowSQL());
		props.put(AvailableSettings.FORMAT_SQL, "true");
		props.put(AvailableSettings.HBM2DDL_AUTO, AppEnv.hibernateDDL());

		var dataToLoad = AppEnv.hibernateDataToLoad();

		if (dataToLoad != null && !dataToLoad.isBlank()) {
			props.put(AvailableSettings.HBM2DDL_IMPORT_FILES, dataToLoad);
		}

		var serviceRegistry = new StandardServiceRegistryBuilder()//
				.applySettings(props)//
				.applySetting(AvailableSettings.DATASOURCE, dataSource) //
				.build();

		var mds = new MetadataSources(serviceRegistry);
		mds.addAnnotatedClass(AbstractEntity.class);
		mds.addAnnotatedClass(AppUser.class);
		mds.addAnnotatedClass(Match.class);
		mds.addAnnotatedClass(Movie.class);
		mds.addAnnotatedClass(QuizzRanking.class);
		mds.addAnnotatedClass(Round.class);
		var metadata = mds.buildMetadata();

		var emf = metadata.getSessionFactoryBuilder().build();

		@SuppressWarnings("deprecation")
		var types = emf.getMetamodel().getManagedTypes().stream().map(mt -> mt.getJavaType()).collect(Collectors.toList());

		log.info("Metamodel :\n{}\n", JSON.pretty(types));

		return emf;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return byBuilder();
	}
}