package com.nc.services.integration;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;

import com.nc.domain.internal.AppUserService;
import com.nc.domain.internal.DomainException;
import com.nc.services.share.BaseSpringTests;
import com.nc.services.share.IntegrationTestConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { IntegrationTestConfig.class })
public class AppUserServiceTest extends BaseSpringTests {

	@Parameters(name = "{0}")
	public static Collection<Object[]> parameters() {
		return List.of(new Object[][]{ { "john" }, { "mary" } });
	}

	@Autowired
	AppUserService userService;

	@Parameter(0)
	public String userName;

	@Test
	public void _01_will_not_find_when_not_bound_and_not_throw_error_if_failOnNotBound_is_false() {
		Assert.assertTrue(userService.count() > 0);
		Assert.assertNull(userService.current(false));
	}

	@Test(expected = DomainException.class)
	public void _02_will_not_find_when_not_bound_and_will_throw_error_if_failOnNotBound_is_true() {
		Assert.assertTrue(userService.count() > 0);
		Assert.assertNull(userService.current());

		Assert.fail();
	}

	@Test
	public void _03_will_find_bound_user() {
		var user = userService.findByName(userName);

		Assert.assertNotNull(user);

		var context = SecurityContextHolder.getContext();

		Assert.assertNotNull(context);
		Assert.assertNull(context.getAuthentication());

		context.setAuthentication(new UsernamePasswordAuthenticationToken(user.getName(), null));

		var bound = userService.current(true);

		Assert.assertEquals(bound.getId(), user.getId());
	}

	@Before
	public void cleanThreadLocals() {
		var context = SecurityContextHolder.getContext();

		if (context == null) {
			SecurityContextHolder.setContext(new SecurityContextImpl());
		} else {
			context.setAuthentication(null);
		}
	}

}