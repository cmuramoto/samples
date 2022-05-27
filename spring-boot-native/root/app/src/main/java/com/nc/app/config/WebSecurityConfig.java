package com.nc.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nc.movie.security.jwt.JwtAuthenticationEntryPoint;
import com.nc.movie.security.jwt.JwtConfig;
import com.nc.movie.security.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ ServicesConfig.class, JwtConfig.class })
@PropertySource({ "classpath:env.properties" })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserDetailsService uds;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// return super.authenticationManagerBean();

		var provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(uds);
		provider.setPasswordEncoder(passwordEncoder);

		var pm = new ProviderManager(provider);

		return pm;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity //
				.csrf().disable() //
				.authorizeRequests() //
				// public paths
				.antMatchers("/v1/authenticate").permitAll() //
				.antMatchers("/v3/api-docs**", "/v3/api-docs/**").permitAll() //
				.antMatchers("/swagger-ui**", "/swagger-ui/**").permitAll() //
				// secured paths
				.anyRequest().authenticated() //
				.and() //
				.exceptionHandling() //
				// plugin Jwt integration
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint()) //
				.and() //
				// force stateless session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add our Jwt Filter before
		var filter = jwtRequestFilter;
		httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		var encoder = passwordEncoder;
		auth //
				.userDetailsService(uds) //
				.passwordEncoder(encoder);
	}
}
