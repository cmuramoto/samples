package com.nc.integration.omdb.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nc.integration.omdb.data.OmdbRecordRepository;
import com.nc.integration.omdb.data.OmdbTermsRepository;
import com.nc.integration.omdb.domain.OmdbRecord;
import com.nc.integration.omdb.domain.OmdbTermStats;

public class OmdbSynchronizationTool {

	Set<String> titles;

	@Autowired
	OmdbRecordRepository records;

	@Autowired
	OmdbTermsRepository terms;

	@Autowired
	OmdbClient client;

	Logger log = LoggerFactory.getLogger(getClass());

	public OmdbSynchronizationTool(Set<String> titles) {
		this.titles = titles;
	}

	OmdbTermStats getOrCreateStats(String title) {
		var term = terms.findOneByTerm(title).orElse(null);

		if (term == null) {
			var head = client.head(title);

			if (head == null || head.isEmpty()) {
				log.warn("Api returned no results for {}", title);
				return null;
			}

			term = new OmdbTermStats();
			term.term = title;
			term.available = head.available;

			log.info("Creating stats \n{}\n", term.toPrettyJson());

			term = terms.save(term);
		} else {
			log.info("Current stats \n{}\n", term.toPrettyJson());
		}
		return term;
	}

	public void incrementalIngest(String title, boolean inflate, long delay) {
		log.info("Incremental Ingesting records for {}", title);
		var term = getOrCreateStats(title);

		if (term == null) {
			return;
		}

		var pages = term.pendingPages();

		pages.forEach(page -> {
			var stats = getOrCreateStats(title);
			var search = client.searchByTitle(title, page);

			log.info("Ingesting records for {} at page {}", title, page);

			if (search == null || search.isEmpty()) {
				log.warn("Api returned no results for page {} and title {}", page, title);
				return;
			}

			for (var record : search.records) {
				lazyIngestRecord(record, inflate, page);
				if (delay > 0) {
					LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(delay));
				}
			}

			stats.pageSynchronized(page);
			stats = terms.save(stats);

			log.info("Page {} for title {} synchronized. Stats \n{}\n", page, title, term.toPrettyJson());

			if (delay > 0) {
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(delay));
			}
		});
	}

	public void ingest(boolean inflate, long delay) {
		for (var title : titles) {
			incrementalIngest(title, inflate, delay);
		}
	}

	private void lazyIngestRecord(OmdbRecord record, boolean inflate, int page) {
		if (!record.isImdbRecord()) {
			return;
		}

		var maybeInflated = records.findOneByImdbID(record.getImdbID()).orElse(null);

		var shoudFetch = maybeInflated == null || (inflate && !maybeInflated.isInflated());

		if (shoudFetch) {
			var inflated = client.inflate(record);

			if (inflated == null) {
				log.warn("Could not inflate record with id {}", record.getImdbID());
			} else {
				inflated = maybeInflated == null ? inflated : maybeInflated.copy(inflated);
				inflated.setPage(page);
				try {
					records.save(inflated);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
					throw e;
				}
			}
		}
	}

}
