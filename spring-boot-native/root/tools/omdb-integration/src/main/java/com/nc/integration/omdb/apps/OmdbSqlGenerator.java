package com.nc.integration.omdb.apps;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nc.integration.omdb.config.TranslationConfig;
import com.nc.integration.omdb.services.OmdbTranslationTool;

public class OmdbSqlGenerator {

	public static void main(String[] args) throws IOException {
		var dst = (args.length == 0 || args[0] == null || args[0].isBlank()) ? "movies.sql" : args[0];

		try (var app = new AnnotationConfigApplicationContext(TranslationConfig.class)) {
			var bean = app.getBean(OmdbTranslationTool.class);

			bean.generateSQL(Paths.get(dst));
		}
	}
}
