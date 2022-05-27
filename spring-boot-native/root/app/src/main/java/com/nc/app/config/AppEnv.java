package com.nc.app.config;

import java.util.HashMap;
import java.util.Map;

public class AppEnv {

	public static String databaseDriver() {
		return getEnvOrProp("DB_DRIVER", "org.h2.Driver");
	}

	public static String databasePass() {
		return getEnvOrProp("DB_PASS", "");
	}

	public static String databaseUrl() {
		return getEnvOrProp("DB_URL", "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
	}

	public static String databaseUser() {
		return getEnvOrProp("DB_USER", "sa");
	}

	static String getEnvOrProp(String key, String def) {
		var rv = System.getenv(key);

		if (rv == null || rv.isBlank()) {
			rv = System.getProperty(key, def);
		}
		return rv;
	}

	public static String hibernateDataToLoad() {
		return getEnvOrProp("HIBERNATE_DATA_TO_LOAD", "users.sql,movies.sql");
	}

	public static String hibernateDDL() {
		return getEnvOrProp("HIBERNATE_DDL", "create-drop");
	}

	public static String hibernateDialect() {
		return getEnvOrProp("HIBERNATE_DIALECT", "org.hibernate.dialect.H2Dialect");
	}

	public static String hibernateShowSQL() {
		return getEnvOrProp("HIBERNATE_SHOW_SQL", "true");
	}

	public static String jwtSecret() {
		return getEnvOrProp("jwt.secret", "supersecret");
	}

	public static String jwtURI() {
		return getEnvOrProp("jwt.get.token.uri", "/authenticate");
	}

	public static Map<String, Object> toProperties() {
		var props = new HashMap<String, Object>();

		props.put("spring.datasource.url", databaseUrl());
		props.put("spring.datasource.driverClassName", databaseDriver());
		props.put("spring.datasource.username", databaseUser());
		props.put("spring.datasource.username", databasePass());

//		props.put("spring.jpa.properties.hibernate.dialect", hibernateDialect());
//		props.put("spring.jpa.properties.hibernate.show_sql", hibernateShowSQL());
//		props.put("spring.jpa.properties.hibernate.format_sql", "true");
//		props.put("spring.jpa.properties.hibernate.hbm2ddl.auto", hibernateDDL());
//
//		props.put("spring.jpa.properties.hibernate.loaded_classes", //
//				List.of( //
//						AbstractEntity.class, //
//						AppUser.class, //
//						Match.class, //
//						Movie.class, //
//						QuizzRanking.class, //
//						Round.class //
//				) //
//		);
//
//		var dataToLoad = hibernateDataToLoad();
//		if (dataToLoad != null && !dataToLoad.isBlank()) {
//			props.put("spring.jpa.properties.hibernate.hbm2ddl.import_files", dataToLoad);
//		}

		props.put("spring.jpa.defer-datasource-initialization", "true");
		props.put("spring.jpa.open-in-view", "false");
		props.put("spring.main.allow-bean-definition-overriding", "false");

		props.put("jwt.get.token.uri", jwtURI());
		props.put("jwt.secret", jwtSecret());

		System.getProperties().putAll(props);

		return props;
	}
}