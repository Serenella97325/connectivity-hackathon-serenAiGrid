package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.network.serenaigrid.medicalDataManagement.services.FHIRDataGeneratorService;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class BundleProvider implements IResourceProvider {

    private static Long bundleCounter = 1L;
    private static final ConcurrentHashMap<String, Bundle> bundles = new ConcurrentHashMap<>();
    
    private final FHIRDataGeneratorService dataGeneratorService;

    @Autowired
    public BundleProvider(FHIRDataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }
    
    static {
        // Inizializza una lista di bundle vuoti
    }

    @Read
    public Bundle findBundle(@IdParam final IdType id) {
        if (bundles.containsKey(id.getIdPart())) {
            return bundles.get(id.getIdPart());
        } else {
            throw new ResourceNotFoundException(id);
        }
    }

    @Create
    public MethodOutcome createBundle(@ResourceParam Bundle bundle) {
    	
        // Genera un ID univoco per il Bundle
    	Bundle bundle1 = dataGeneratorService.generateFHIRBundle();
    	
    	bundle = bundle1;
    	
    	bundle.setId(createId("Bundle", bundleCounter, 1L));
        bundles.put(String.valueOf(bundleCounter), bundle);
        bundleCounter++;

        // Restituisci un risultato positivo con l'ID generato
        return new MethodOutcome(bundle.getIdElement());
    }
    
    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }

    /**
     * Metodo per creare un ID specifico per una risorsa FHIR
     */
    private static IdType createId(final String resourceType, final Long id, final Long versionId) {
        return new IdType(resourceType, "" + id, "" + versionId);
    }
}
