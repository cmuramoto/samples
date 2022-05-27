package com.nc.app.test;

import java.lang.StackWalker.Option;
import java.net.Socket;

import org.junit.Before;

public class RemoteApiIntegrationTests extends BaseApiIntegrationTests {

	static Integer checkedPort;

	@Before
	@Override
	public void _000_checkState() {
		if (checkedPort == null) {
			check("localhost", 8080);
		}
		port = checkedPort;
		super._000_checkState();
	}

	private void check(String host, int port) {
		var top = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).walk(frames -> frames.reduce((l, r) -> r).orElse(null));

		if (top == null) {
			log.warn("Unable to determine top frame!");
			checkedPort = -1;
			return;
		}

		if (!top.getClassName().equals("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner")) {
			log.warn("Test supposed to run in ide. {}", top.getClassName());
			checkedPort = -1;
			return;
		}

		log.info("Proceeding with socket check");

		try (var socket = new Socket(host, port)) {
			STATE.reset();
			checkedPort = port;
		} catch (Exception e) {
			log.warn("Unable to connect ({}:{} => {}). Tests won't run", host, port, e.getMessage());
			checkedPort = -1;
		}
	}

}
