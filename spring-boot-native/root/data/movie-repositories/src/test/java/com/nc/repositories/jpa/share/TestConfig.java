package com.nc.repositories.jpa.share;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:test.properties" })
@ImportResource("classpath:persistence.xml")
@EnableJpaRepositories(basePackages = "com.nc.repositories.jpa")
public class TestConfig {

}
