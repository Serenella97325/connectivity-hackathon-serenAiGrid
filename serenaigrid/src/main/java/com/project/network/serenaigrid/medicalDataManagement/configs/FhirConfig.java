package com.project.network.serenaigrid.medicalDataManagement.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfig {

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();  // Crea e restituisce il FhirContext per la versione R4 di FHIR
    }
    
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    
}