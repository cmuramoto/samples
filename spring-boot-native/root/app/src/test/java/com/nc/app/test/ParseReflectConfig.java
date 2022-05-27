package com.nc.app.test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springdoc.core.Constants;

import com.nc.app.test.ParseReflectConfig.ReflectConfig.Field;
import com.nc.utils.json.JSON;

public class ParseReflectConfig {

	static class ReflectConfig {
		static class Field {
			String name;
			Boolean allowWrite;
		}

		static class Method {
			String name;

			String[] parameterTypes;
		}

		String name;

		Boolean allDeclaredFields;

		Boolean queryAllDeclaredMethods;

		Boolean queryAllPublicMethods;

		Boolean queryAllDeclaredConstructors;

		Field[] fields;

		Method[] methods;

		Method[] queriedMethods;
	}

	static final Set<String> UNCONDITIONAL = Set.of("org.springframework.data.jpa.repository.support.SimpleJpaRepository");

	static final List<String> FILTERED_PREFIXES = List.of( //
			// "[", //
			"java.", //
			"sun.", //
			"org.graalvm", //
			"org.springframework", //
			"org.eclipse.jdt", //
			"org.springdoc.core.Constants" //
	) //
			.stream() //
			.map(term -> "^" + Pattern.quote(term)) //
			.toList();

	static final List<String> FILTERED_TERMS = List.of( //
			"$$SpringProxy$", //
			"$$EnhancerBySpringCGLIB$", //
			"$$KeyFactoryByCGLIB$$", //
			"$$FastClassBySpringCGLIB"//
	)//
			.stream() //
			.map(term -> Pattern.quote(term)) //
			.toList();

	static final Pattern FILTER = Pattern.compile("(" + //
			Stream.concat(FILTERED_PREFIXES.stream(), FILTERED_TERMS.stream()).collect(Collectors.joining("|")) + //
			")");

	static boolean accept(ReflectConfig config) {
		var name = config.name;

		var ok = name != null && (UNCONDITIONAL.contains(name) || !FILTER.matcher(name).find());

		return ok;
	}

	public static void main(String... args) throws IOException {
		var source = Paths.get("native-image-agent/reflect-config.json");
		var dest = source.resolveSibling("reflect-config-filtered.json");
		var configs = JSON.parse(ReflectConfig[].class, source);

		var filtered = Arrays.stream(configs).filter(ParseReflectConfig::accept).collect(Collectors.toList());

		var fields = Constants.class.getDeclaredFields();

		var r = new ReflectConfig();
		r.queryAllPublicMethods = true;
		r.name = Constants.class.getName();
		r.fields = Arrays.stream(fields).map(f -> {
			var field = new Field();
			field.name = f.getName();
			return field;
		}).toArray(Field[]::new);

		filtered.add(r);

		JSON.encode(filtered, dest, true);
	}

}
