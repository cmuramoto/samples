package com.nc.integration.omdb.apps;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nc.integration.omdb.config.IngestionConfig;
import com.nc.integration.omdb.services.OmdbSynchronizationTool;

public class OmdbIngestor {

	public static void main(String[] args) {
		try (var app = new AnnotationConfigApplicationContext(IngestionConfig.class)) {
			var bean = app.getBean(OmdbSynchronizationTool.class);

			bean.ingest(true, Long.getLong("omdb.translator.backoff.ms", 15000));
		}
	}

}
