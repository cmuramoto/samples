package org.springframework.boot.logging.logback;

import org.springframework.boot.logging.LoggingSystem;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.ContextAwareBase;

public class LogbackCustomConfigurator extends ContextAwareBase implements Configurator {

	@Override
	public void configure(LoggerContext context) {
		context.putObject(LoggingSystem.class.getName(), new Object());

		var configurator = new LogbackConfigurator(context);
		var console = console(context);
		configurator.appender("Console", console);
		configurator.root(Level.INFO, console);
		configurator.logger("org.springframework", Level.INFO, false, console);
		configurator.logger("org.hibernate", Level.INFO, false, console);

		var config = new DefaultLogbackConfiguration(null);
		config.apply(configurator);
		context.setPackagingDataEnabled(true);

		context.start();
	}

	private Appender<ILoggingEvent> console(LoggerContext context) {
		var c = new ConsoleAppender<ILoggingEvent>();
		c.setContext(context);
		var layout = new PatternLayout();
		layout.setContext(context);
		layout.setPattern("[%d{ISO8601}][%level][%t][%C]:[%msg%throwable]%n");
		layout.start();

		c.setLayout(layout);

		return c;
	}

}
