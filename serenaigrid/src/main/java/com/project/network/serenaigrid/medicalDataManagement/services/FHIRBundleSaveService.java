package com.project.network.serenaigrid.medicalDataManagement.services;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.network.serenaigrid.medicalDataManagement.providers.BundleProvider;
import com.project.network.serenaigrid.medicalDataManagement.providers.DeviceProvider;
import com.project.network.serenaigrid.medicalDataManagement.providers.ObservationProvider;
import com.project.network.serenaigrid.utils.ApiResponse;

import jakarta.transaction.Transactional;

@Service
public class FHIRBundleSaveService {

	@Autowired
	private FHIRDataGeneratorService fhirDataGeneratorService;

	@Autowired
	private ObservationProvider observationProvider; // Provider per Observation
	
	@Autowired
	private DeviceProvider deviceProvider; // Provider per Device
	
	@Autowired
	private BundleProvider bundleProvider; // Bundle per Device


	/**
	 * Genera un FHIR Bundle, salva le risorse e gli ID nel database
	 */
	@Transactional
	public ApiResponse<Bundle> generateAndSaveFHIRBundle() {
		try {
			// Genera il FHIR Bundle
			Bundle bundle = fhirDataGeneratorService.generateFHIRBundle();

			// Salva le risorse nel database (osservazioni, dispositivi, ecc.)
			saveBundle(bundle);

	        // Salva il bundle completo
	        bundleProvider.createBundle(bundle);

	        // Salva il bundle completo
	        bundleProvider.createBundle(bundle);

	        // Crea una risposta di successo
	        return ApiResponse.success(bundle);

		} catch (Exception e) {
	        e.printStackTrace();
	        // Crea una risposta di errore
	        return ApiResponse.failure("Errore durante la generazione e il salvataggio del Bundle: " + e.getMessage());
	    }
	}

	/**
	 * Salva le risorse (Observation, Device) nel database
	 */
	@Transactional
	public void saveBundle(Bundle bundle) {

		// Itera su tutte le voci del bundle e salva le risorse nel database
		for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
			if (entry.getResource() instanceof Observation) {

				Observation observation = (Observation) entry.getResource();
				// Usa il provider per la creazione dell'osservazione
				observationProvider.createObservation(observation);

			} else if (entry.getResource() instanceof Device) {

				Device device = (Device) entry.getResource();
				// Usa il provider per la creazione del dispositivo
				deviceProvider.createDevice(device);
			}

		}
	}
}
