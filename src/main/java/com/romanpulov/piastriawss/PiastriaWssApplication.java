package com.romanpulov.piastriawss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PiastriaWssApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(PiastriaWssApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PiastriaWssApplication.class);
	}
}
