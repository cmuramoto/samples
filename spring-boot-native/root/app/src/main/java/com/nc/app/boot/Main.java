package com.nc.app.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.logging.logback.ColorConverter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.AotProxyHints;
import org.springframework.nativex.hint.InitializationHint;
import org.springframework.nativex.hint.InitializationHints;
import org.springframework.nativex.hint.InitializationTime;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.nc.app.config.AppEnv;
import com.nc.app.config.WebSecurityConfig;
import com.nc.services.internal.AppUserServiceImp;

@Configuration
@Import({ WebSecurityConfig.class })
@SpringBootApplication(proxyBeanMethods = true)
@ComponentScan({ "com.nc.app.api.v1" })
@InitializationHints({ //
		@InitializationHint(types = ColorConverter.class, initTime = InitializationTime.BUILD) //
})
@AotProxyHints({ //
		@AotProxyHint(//
				targetClass = AppUserServiceImp.class, //
				interfaces = { org.springframework.aop.scope.ScopedObject.class, java.io.Serializable.class, UserDetailsService.class }//
		)//
})
@EnableAutoConfiguration(exclude = { //
		SqlInitializationAutoConfiguration.class, HibernateJpaAutoConfiguration.class, //
		UserDetailsServiceAutoConfiguration.class, BatchAutoConfiguration.class //
})
public class Main {

	public static void main(String[] args) {
		try {
			var app = new SpringApplication(Main.class);

			app.setDefaultProperties(AppEnv.toProperties());

			app.run(args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}