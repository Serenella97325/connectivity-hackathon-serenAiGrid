package com.project.network.serenaigrid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.project.network.serenaigrid.medicalDataManagement.configs.FhirRestfulServer;

@SpringBootApplication
public class SerenaigridApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerenaigridApplication.class, args);
	}
	
    @Bean
    public ServletRegistrationBean<FhirRestfulServer> servletRegistrationBean(ApplicationContext context) {
        FhirRestfulServer restfulServer = new FhirRestfulServer(context);
        ServletRegistrationBean<FhirRestfulServer> registration = new ServletRegistrationBean<>(restfulServer, "/*");
        registration.setName("FhirServlet");
        return registration;
    }

}
