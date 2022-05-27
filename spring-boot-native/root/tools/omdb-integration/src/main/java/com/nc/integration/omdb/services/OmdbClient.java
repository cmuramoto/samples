package com.nc.integration.omdb.services;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.nc.integration.omdb.domain.OmdbRecord;
import com.nc.integration.omdb.domain.OmdbSearch;

import reactor.core.publisher.Mono;

public class OmdbClient {

	static {
		System.setProperty("io.netty.transport.noNative", "true");
	}

	WebClient client;
	String apiKey;
	int maxPerPage;

	public OmdbClient(String baseUrl, String apiKey, int maxPerPage) {
		this.client = WebClient.builder() //
				.baseUrl(baseUrl) //
				.defaultHeaders(sink -> {
					sink.add("Accept", MediaType.APPLICATION_JSON_VALUE);
				}) //
				.build();
		this.apiKey = apiKey;
		this.maxPerPage = maxPerPage;
	}

	public OmdbSearch head(String title) {
		return searchByTitle(title, 1);
	}

	public OmdbRecord inflate(OmdbRecord record) {
		var id = record.getImdbID();

		if (id != null && !id.isBlank()) {
			var result = this.client //
					.get() //
					.uri(builder -> builder //
							.queryParam("apiKey", this.apiKey) //
							.queryParam("plot", "short") //
							.queryParam("i", id) //
							.build()) //
					.exchangeToMono(res -> {
						if (res.statusCode().is2xxSuccessful()) {
							return res.bodyToMono(OmdbRecord.class);
						}
						return res.createException().flatMap(Mono::error);
					});

			record = result.block();
		}

		return record;
	}

	private int requestsNeeded(OmdbSearch head) {
		var available = head.available;

		return available / maxPerPage + (available % maxPerPage == 0 ? 0 : 1);
	}

	public void save(String title, Consumer<OmdbRecord> sink, long delayMs) {
		var page = new int[]{ 1 };
		var log = LoggerFactory.getLogger(getClass());

		stream(title, delayMs).flatMap(search -> {
			log.info("Inflating records of page {}", page);

			var records = search.records;

			if (records != null) {
				return records.stream().map(record -> inflate(record));
			}

			return Stream.empty();
		}).forEach(sink);
	}

	public OmdbSearch searchByTitle(String title, int page) {
		var result = this.client //
				.get() //
				.uri(builder -> builder //
						.queryParam("apiKey", this.apiKey) //
						.queryParam("s", title) //
						.queryParam("type", "movie") //
						.queryParam("page", page) //
						.build()) //
				.exchangeToMono(res -> {
					if (res.statusCode().is2xxSuccessful()) {
						return res.bodyToMono(OmdbSearch.class);
					}
					return res.createException().flatMap(Mono::error);
				});

		return result.block();
	}

	public Stream<OmdbSearch> stream(String title, long delayMs) {
		var head = searchByTitle(title, 1);

		if (head == null || head.isEmpty()) {
			return Stream.empty();
		}

		var pages = requestsNeeded(head);

		var first = Stream.of(head);

		if (pages >= 2) {
			var rest = IntStream.rangeClosed(2, pages).mapToObj(page -> {
				if (delayMs > 0) {
					LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(delayMs));
				}
				return searchByTitle(title, page);
			});

			first = Stream.concat(first, rest);
		}

		return first;
	}

}
