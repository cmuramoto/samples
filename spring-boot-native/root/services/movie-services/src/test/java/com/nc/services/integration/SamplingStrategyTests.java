package com.nc.services.integration;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.nc.domain.internal.MovieSamplingStrategy;
import com.nc.domain.internal.MovieService;
import com.nc.domain.internal.Pair;
import com.nc.services.share.BaseSpringTests;
import com.nc.services.share.IntegrationTestConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { IntegrationTestConfig.class })
public class SamplingStrategyTests extends BaseSpringTests {

	@Parameters(name = "{0}")
	public static Collection<Object[]> parameters() {
		return List.of(new Object[][]{ { 5 }, { 10 }, { 20 }, { 100 } });
	}

	@Parameter(0)
	public int rounds;

	@Autowired
	MovieSamplingStrategy strategy;

	@Autowired
	MovieService movies;

	@Test
	public void _01_will_generate_pairs_when_sampling_is_less_than_max_movies() {
		var count = movies.count();

		Assume.assumeTrue(count > rounds);

		var seen = new TreeSet<Pair>();

		while (seen.size() < rounds) {
			var pair = strategy.pickNext(seen, rounds);

			Assert.assertNotNull(pair);
			Assert.assertTrue(seen.add(pair));
		}

	}
}
