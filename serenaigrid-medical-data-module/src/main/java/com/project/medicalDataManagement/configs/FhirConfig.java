package com.project.medicalDataManagement.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfig {

	@Bean
	public FhirContext fhirContext() {
		return FhirContext.forR4(); // Creates and returns the FhirContext for R4 version of FHIR
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}