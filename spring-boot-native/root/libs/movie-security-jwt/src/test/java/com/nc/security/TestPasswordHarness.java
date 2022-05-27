package com.nc.security;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordHarness {

	@Test
	public void run() {
		var encoder = new BCryptPasswordEncoder();

		Assert.assertTrue(encoder.matches("secret", "$2a$10$0c0KCEJBYS2F6iXv1Wq6QuOjkPh6INn2zj6l4pL83w5bcRHiehZyq"));
		Assert.assertTrue(encoder.matches("changeme", "$2a$10$MC4wyO0rQWZzUtnsAV17f.AgYfkVqUVYiiBNNTjnBd5jgHsNtlkDq"));
	}

}
