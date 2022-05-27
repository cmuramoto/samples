package com.nc.app.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nc.app.boot.Main;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiIntegrationTests extends BaseApiIntegrationTests {

	@Before
	@Override
	public void _000_checkState() {
		super._000_checkState();
	}

}
