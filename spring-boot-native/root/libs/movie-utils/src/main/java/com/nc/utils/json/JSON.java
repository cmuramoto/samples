package com.nc.utils.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JSON {

	static final DefaultPrettyPrinter pp;

	static Module[] mods;

	static ObjectMapper m;

	// {}
	static final byte[] EMPTY = new byte[]{ 123, 125 };

	static {
		pp = new DefaultPrettyPrinter();
		pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
	}

	public static <T> T convert(Class<? extends T> type, Map<String, Object> chunk) {
		return mapper().convertValue(chunk, type);
	}

	public static <T> T convert(Class<? extends T> type, Object json) {
		return mapper().convertValue(json, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertOpaque(Class<? extends T> type, Object opaque) {
		return convert(type, (Map<String, Object>) opaque);
	}

	static <T> T doParse(Class<? extends T> type, InputStream in) throws IOException {
		return mapper().readValue(in, type);
	}

	public static byte[] empty() {
		return EMPTY;
	}

	public static byte[] encode(Object val) {
		try {
			return mapper().writeValueAsBytes(val);
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> void encode(T val, Appendable dst, boolean pretty) {
		if (dst instanceof Writer) {
			encode(val, (Writer) dst, pretty);
		} else if (dst instanceof PrintStream) {
			encode(val, (PrintStream) dst, pretty);
		} else {
			var s = pretty ? pretty(val) : stringify(val);
			try {
				dst.append(s);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static <T> void encode(T val, OutputStream dst) {
		encode(val, dst, false);
	}

	public static <T> void encode(T val, OutputStream dst, boolean pretty) {
		try {
			writer(pretty).writeValue(dst, val);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> void encode(T val, Path dst) {
		encode(val, dst, false);
	}

	public static <T> void encode(T val, Path dst, boolean pretty) {
		try (var out = Files.newOutputStream(dst, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			encode(val, out, pretty);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> void encode(T val, PrintStream dst, boolean pretty) {
		try {
			writer(pretty).writeValue(dst, val);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> void encode(T val, Writer dst, boolean pretty) {
		try {
			writer(pretty).writeValue(dst, val);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static byte[] encodePretty(Object val) {
		try {
			return mapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(val);
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	static String[] externalModules() {
		return new String[]{ "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.JavaTimeModule" };
	}

	public static ObjectMapper mapper() {
		ObjectMapper rv = m;

		if (rv == null) {
			rv = m = newMapper();
		}

		return rv;
	}

	private static synchronized Module[] modules() {
		Module[] rv = mods;
		if (rv == null) {
			var module = new JavaTimeModule();
			rv = new Module[]{ module };
		}
		return rv;
	}

	public static ObjectMapper newMapper() {
		return newMapper(JsonInclude.Include.NON_EMPTY);
	}

	public static ObjectMapper newMapper(JsonInclude.Include include) {
		return newMapper(include, 0);
	}

	public static ObjectMapper newMapper(JsonInclude.Include include, int mode) {
		var rv = new ObjectMapper();

		JsonAutoDetect.Visibility fields;
		JsonAutoDetect.Visibility props;
		Visibility creator = switch (mode) {
		case 0 -> {
			fields = JsonAutoDetect.Visibility.ANY;
			props = JsonAutoDetect.Visibility.NONE;
			yield JsonAutoDetect.Visibility.NONE;
		}
		case 1 -> {
			fields = JsonAutoDetect.Visibility.NONE;
			props = JsonAutoDetect.Visibility.ANY;
			yield JsonAutoDetect.Visibility.NONE;
		}
		case 2 -> {
			fields = JsonAutoDetect.Visibility.ANY;
			props = JsonAutoDetect.Visibility.ANY;
			yield JsonAutoDetect.Visibility.NONE;
		}
		default -> {
			fields = JsonAutoDetect.Visibility.ANY;
			props = JsonAutoDetect.Visibility.ANY;
			yield JsonAutoDetect.Visibility.ANY;
		}
		};

		rv.setVisibility(rv.getSerializationConfig().getDefaultVisibilityChecker().//
				withFieldVisibility(fields).withGetterVisibility(props).//
				withSetterVisibility(props).withCreatorVisibility(creator).//
				withIsGetterVisibility(props));

		rv.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		rv.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		rv.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		rv.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		rv.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

		rv.setSerializationInclusion(include);

		var mods = modules();
		if (mods != null && mods.length > 0) {
			rv.registerModules(mods);
		}

		return rv;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(byte[] chunk) {
		return parse(Map.class, chunk);
	}

	public static <T> T parse(Class<? extends T> type, byte[] chunk) {
		try {
			return mapper().readValue(chunk, type);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> T parse(Class<? extends T> type, ClassLoader cl, String resource) {
		return parse(type, cl.getResource(resource));
	}

	public static <T> T parse(Class<? extends T> type, InputStream in) {
		try {
			return mapper().readValue(in, type);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> T parse(Class<? extends T> type, Path path) {
		try (var is = Files.newInputStream(path)) {
			return doParse(type, is);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> T parse(Class<? extends T> type, Reader in) {
		try {
			return mapper().readValue(in, type);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> T parse(Class<? extends T> type, String s) {
		try {
			return mapper().readValue(s, type);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static <T> T parse(Class<? extends T> type, URL resource) {
		try (var is = resource.openStream()) {
			return doParse(type, is);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(InputStream in) {
		return parse(Map.class, in);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(Path path) {
		return parse(Map.class, path);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(Reader in) {
		return parse(Map.class, in);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(String s) {
		return parse(Map.class, s);
	}

	public static <T> T parseResource(Class<? extends T> type, String resource) {
		return parse(type, type.getClassLoader().getResource(resource));
	}

	public static <T> String pretty(T val) {
		try {
			return mapper().writer(pp).writeValueAsString(val);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public static <T> String stringify(Class<? extends T> projection, T val) {
		try {
			return mapper().writerFor(projection).writeValueAsString(val);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public static <T> String stringify(T val) {
		try {
			return mapper().writeValueAsString(val);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object o) {
		return mapper().convertValue(o, HashMap.class);
	}

	@SuppressWarnings("unchecked")
	public static NavigableMap<String, Object> toNavigableMap(Object o) {
		return mapper().convertValue(o, TreeMap.class);
	}

	static ObjectWriter writer(boolean pretty) {
		return pretty ? mapper().writerWithDefaultPrettyPrinter() : mapper().writer();
	}
}
