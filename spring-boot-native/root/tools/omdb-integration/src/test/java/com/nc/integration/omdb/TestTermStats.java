package com.nc.integration.omdb;

import org.junit.Assert;
import org.junit.Test;

import com.nc.integration.omdb.domain.OmdbTermStats;

public class TestTermStats {

	@Test
	public void willStreamCorrectNumberOfPendingPages() {
		var stats = new OmdbTermStats();
		stats.available = 37;

		Assert.assertEquals(4, stats.totalPages());
		Assert.assertEquals(1, stats.currentPage());

		var pages = stats.pendingPages().toArray();

		Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, pages);

		stats.sync = 1;
		Assert.assertEquals(1, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, pages);

		stats.sync = 9;
		Assert.assertEquals(1, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, pages);

		stats.sync = 10;
		Assert.assertEquals(2, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 2, 3, 4 }, pages);

		stats.sync = 29;
		Assert.assertEquals(3, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 3, 4 }, pages);

		stats.sync = 30;
		Assert.assertEquals(4, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 4 }, pages);

		stats.sync = 36;
		Assert.assertEquals(4, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[]{ 4 }, pages);

		stats.sync = 37;
		Assert.assertEquals(4, stats.currentPage());
		pages = stats.pendingPages().toArray();
		Assert.assertArrayEquals(new int[0], pages);
	}
}
