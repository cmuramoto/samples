package com.nc.integration.omdb.apps;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nc.integration.omdb.config.TranslationConfig;
import com.nc.integration.omdb.services.OmdbTranslationTool;

public class OmdbTranslator {

	public static void main(String[] args) {
		try (var app = new AnnotationConfigApplicationContext(TranslationConfig.class)) {
			var bean = app.getBean(OmdbTranslationTool.class);

			bean.translate();
		}
	}
}
