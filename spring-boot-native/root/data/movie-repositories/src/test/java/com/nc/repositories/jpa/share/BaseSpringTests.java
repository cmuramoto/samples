package com.nc.repositories.jpa.share;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

public abstract class BaseSpringTests extends AbstractJUnit4SpringContextTests {

	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

}
