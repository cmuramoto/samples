package com.nc.integration.omdb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.nc.integration.omdb.services.OmdbTranslationTool;

@Configuration
@Import(CommonConfig.class)
@EnableJpaRepositories(basePackages = { "com.nc.integration.omdb", "com.nc.repositories.jpa" })
public class TranslationConfig {

	Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public OmdbTranslationTool tool() {
		return new OmdbTranslationTool();
	}

}
