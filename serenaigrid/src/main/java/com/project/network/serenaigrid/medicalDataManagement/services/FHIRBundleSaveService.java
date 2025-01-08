package com.project.network.serenaigrid.medicalDataManagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.network.serenaigrid.medicalDataManagement.models.DeviceDO;
import com.project.network.serenaigrid.medicalDataManagement.models.FHIRBundleDO;
import com.project.network.serenaigrid.medicalDataManagement.models.ObservationDO;
import com.project.network.serenaigrid.medicalDataManagement.repositories.DeviceRepository;
import com.project.network.serenaigrid.medicalDataManagement.repositories.FHIRBundleRepository;
import com.project.network.serenaigrid.medicalDataManagement.repositories.ObservationRepository;
import com.project.network.serenaigrid.utils.ApiResponse;

import ca.uhn.fhir.context.FhirContext;
import jakarta.transaction.Transactional;

@Service
public class FHIRBundleSaveService {

    @Autowired
    private FHIRDataGeneratorService fhirDataGeneratorService;

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FHIRBundleRepository fhirBundleRepository;
    
    @Autowired
    private FhirContext fhirContext;
    
    @Autowired
    private ObjectMapper objectMapper; 

    /**
     * Genera un FHIR Bundle, salva le risorse e gli ID nel database
     * Restituisce un ApiResponse con i risultati o eventuali errori.
     */
    @Transactional
    public ApiResponse<FHIRBundleDO> generateAndSaveFHIRBundle() {
        try {
            // Genera il FHIR Bundle
            Bundle bundle = fhirDataGeneratorService.generateFHIRBundle();

            // Salva le risorse nel database (osservazioni, dispositivi, ecc.)
            saveBundle(bundle);
            
            // Serializza l'intero bundle in JSON
            String bundleJson = fhirContext.newJsonParser()
            		.setPrettyPrint(true) // Abilita la formattazione con indentazione
            		.encodeResourceToString(bundle);
            
            // Prettifica il JSON prima di restituirlo (facoltativo)
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bundleJson);

            // Estrai gli ID delle risorse
            List<String> observationIds = bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof Observation)
                    .map(entry -> entry.getResource().getIdElement().getIdPart())
                    .collect(Collectors.toList());

            List<String> deviceIds = bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof Device)
                    .map(entry -> entry.getResource().getIdElement().getIdPart())
                    .collect(Collectors.toList());

            // Crea un nuovo FHIRBundle con ID e JSON completo
            FHIRBundleDO fhirBundle = new FHIRBundleDO();
            fhirBundle.setObservationIds(observationIds);
            fhirBundle.setDeviceIds(deviceIds);
            fhirBundle.setBundleJson(prettyJson);

            // Salva gli ID nel repository
            fhirBundleRepository.save(fhirBundle);
            
            // Restituisci un ApiResponse con successo
            return ApiResponse.success(fhirBundle);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Errore durante la generazione e il salvataggio del FHIR Bundle: " + e.getMessage());
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
            	
            	// Serializza l'osservazione in JSON
            	Observation observation = (Observation) entry.getResource();
                String observationJson = fhirContext.newJsonParser()
                		.encodeResourceToString(observation);
                
                // Salva l'osservazione serializzata nel database
                ObservationDO observationDO = ObservationDO.builder()
                		.observationJson(observationJson)
                		.build();
                observationRepository.save(observationDO);
            } else if (entry.getResource() instanceof Device) {
            	
                // Serializza il dispositivo in JSON
                Device device = (Device) entry.getResource();
                String deviceJson = fhirContext.newJsonParser()
                		.encodeResourceToString(device);
                
                // Salva il dispositivo serializzato nel database
                DeviceDO deviceDO = DeviceDO.builder()
                        .deviceJson(deviceJson)
                        .build();
                deviceRepository.save(deviceDO);
            }
            // Puoi aggiungere altre risorse qui se necessario
        }
    }
}
