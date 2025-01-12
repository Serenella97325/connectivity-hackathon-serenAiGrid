package com.project.network.serenaigrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@ServletComponentScan
@SpringBootApplication
public class SerenaigridApplication extends SpringBootServletInitializer {
	
	private static final Class<SerenaigridApplication> applicationClass = SerenaigridApplication.class;

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

}
