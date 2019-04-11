package com.bastronaut.bigspender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BigspenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigspenderApplication.class, args);
	}

}
