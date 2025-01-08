package com.project.network.serenaigrid.medicalDataManagement.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfig {
	
	// Creazione di un FHIRContext per la versione FHIR R4
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

}
