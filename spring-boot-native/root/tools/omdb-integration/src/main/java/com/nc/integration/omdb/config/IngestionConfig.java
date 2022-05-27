package com.nc.integration.omdb.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.nc.integration.omdb.domain.OmdbTermStats;
import com.nc.integration.omdb.services.OmdbClient;
import com.nc.integration.omdb.services.OmdbSynchronizationTool;
import com.nc.utils.json.JSON;

@Configuration
@Import(CommonConfig.class)
@EnableJpaRepositories(basePackages = "com.nc.integration.omdb")
public class IngestionConfig {

	Logger log = LoggerFactory.getLogger(getClass());

	@Value("${omdb.apiKey}")
	String omdbApiKey;

	@Value("${omdb.url}")
	String omdbUrl;

	@Bean
	public OmdbClient client() {
		return new OmdbClient(omdbUrl, omdbApiKey, OmdbTermStats.API_RESULTS_PER_PAGE);
	}

	@Bean
	public OmdbSynchronizationTool tool() {
		return new OmdbSynchronizationTool(resolveTitles());
	}

	public Set<String> resolveTitles() {
		var prop = System.getProperty("omdb.integration.titles");

		if (prop == null || (prop = prop.trim()).isBlank()) {
			log.info("No titles provided by sysprop omdb.integration.titles");

			var def = new TreeSet<>(List.of("Batman", "Matrix", "Beethoven", "God", "Furious", "X-men", "Superman", "Boss", "Mob"));

			log.info("Using default titles \n{}\n", JSON.pretty(def));

			return def;
		} else {
			try {
				var p = Paths.get(prop);
				return new LinkedHashSet<>(Files.readAllLines(p));

			} catch (Exception e) {
				log.info("Seems like prop does not point to a valid file. Assuming csv string");
				return Arrays.stream(prop.split(",")).collect(Collectors.toSet());
			}
		}
	}
}
