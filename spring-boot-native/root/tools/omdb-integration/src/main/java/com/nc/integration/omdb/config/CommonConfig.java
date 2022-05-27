package com.nc.integration.omdb.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.nc.integration.omdb")
@EnableTransactionManagement
@PropertySource({ "classpath:app.properties" })
@ImportResource("classpath:persistence.xml")
public class CommonConfig {

}
