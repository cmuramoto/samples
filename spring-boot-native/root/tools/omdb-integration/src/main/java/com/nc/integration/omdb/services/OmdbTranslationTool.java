package com.nc.integration.omdb.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.nc.domain.internal.Movie;
import com.nc.integration.omdb.data.OmdbRecordRepository;
import com.nc.integration.omdb.domain.OmdbRating;
import com.nc.integration.omdb.domain.OmdbRecord;
import com.nc.repositories.jpa.internal.MovieRepository;

public class OmdbTranslationTool {

	static String clean(String s) {
		return s == null ? null : s.replace("'", "");
	}

	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	MovieRepository movies;

	@Autowired
	OmdbRecordRepository records;

	public void generateSQL(Path dst) throws IOException {

		var universe = movies.findAll();

		try (var writer = Files.newBufferedWriter(dst, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			for (var movie : universe) {
				var sql = toSql(movie);

				writer.write(sql);
			}
		}
	}

	Float parseFloat(String field, String v) {
		if (v == null || (v = v.trim()).isBlank()) {
			return null;
		}
		try {
			return Float.parseFloat(v);
		} catch (Exception e) {
			log.warn("{} has unexpected format {}", field, v);
		}

		try {
			return Float.parseFloat(v.substring(0, v.indexOf('/')));
		} catch (Exception e) {
			log.warn("{} has unexpected format {}", field, v);
		}

		return null;
	}

	Float parseScore(OmdbRecord record, OmdbRating rating) {
		return parseFloat("Rating", record.getImdbRating());
	}

	Integer parseTotalVotes(OmdbRecord record) {
		try {
			var value = record.getImdbVotes();
			return Integer.valueOf(value.replace(",", ""));
		} catch (Exception e) {
			log.warn("Vote has unexpected format {}", record.getImdbVotes());
			return null;
		}
	}

	private String toSql(Movie movie) {
		return String.format("insert into Movie (id,title,imdbID,score,totalVotes,poster) values (%d,'%s','%s',%.2f,%d,'%s');\n", movie.getId(), clean(movie.getTitle()), movie.getImdbID(), movie.getScore(), movie.getTotalVotes(), clean(movie.getPoster()));
	}

	public void translate() {
		movies.deleteAll();
		LockSupport.parkNanos(1_000_000_000L);

		var total = records.count();

		var pagesize = 50;
		var pages = total / pagesize + ((total % pagesize) == 0 ? 0 : 1);

		log.info("Synchronizing {} records in pages of {}. Total Pages: {}", total, pagesize, pages);

		var rem = total;

		for (var page = 0; page < pages; page++) {
			var req = PageRequest.of(page, pagesize, Sort.by("id").ascending());

			log.info("Sinchronizing Page {}", page);

			var slice = records.findAll(req);

			for (var record : slice) {
				var movie = translate(record);

				if (movie != null) {
					movies.save(movie);
				}
			}

			rem = Math.max(0, rem - pagesize);
			log.info("Sinchronized Page {}. Remaining {}", page, rem);
		}

		log.info("Translation finished! Movies: {}", movies.count());
		LockSupport.parkNanos(1_000_000_000L);
	}

	private Movie translate(OmdbRecord record) {
		if (!record.isImdbRecord()) {
			return null;
		}

		var ratings = record.getRatings();

		if (ratings == null) {
			return null;
		}

		var rating = ratings.stream().filter(r -> r.isImdb()).findFirst().orElse(null);

		if (rating == null) {
			return null;
		}

		var score = parseScore(record, rating);

		if (score == null) {
			return null;
		}

		var totalVotes = parseTotalVotes(record);

		if (totalVotes == null) {
			return null;
		}

		var movie = new Movie();
		movie.setTitle(record.getTitle());
		movie.setImdbID(record.getImdbID());
		movie.setScore(score);
		movie.setTotalVotes(totalVotes);
		movie.setPoster(record.getPoster());

		return movie;
	}
}