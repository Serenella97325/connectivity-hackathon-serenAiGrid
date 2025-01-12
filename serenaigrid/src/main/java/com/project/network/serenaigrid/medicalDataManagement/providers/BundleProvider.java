package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.project.network.serenaigrid.medicalDataManagement.services.FHIRDataGeneratorService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
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
    private final RestTemplate restTemplate;
    private final FhirContext fhirContext;
    
    @Autowired
    public BundleProvider(FHIRDataGeneratorService dataGeneratorService, RestTemplate restTemplate, FhirContext fhirContext) {
        this.dataGeneratorService = dataGeneratorService;
        this.restTemplate = restTemplate;
        this.fhirContext = fhirContext;
    }
    
    @Read
    public Bundle findBundle(@IdParam final IdType id) {
        if (bundles.containsKey(id.getIdPart())) {
        	
            // Retrieve the bundle from repository
            Bundle bundle = bundles.get(id.getIdPart());

            // Now we send the bundle to Python for processing
            String pythonServerUrl = "http://localhost:5000/process-bundle"; // URL of the Python server

            // Serialize the Bundle in JSON
            IParser parser = fhirContext.newJsonParser();
            String jsonBundle = parser.encodeResourceToString(bundle);
            
			System.out.println("JsonBundle: " + jsonBundle);

			// Set the headers (Content-Type: application/json) 
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");

			// Send data to the Python server for processing  
			HttpEntity<String> requestEntity = new HttpEntity<>(jsonBundle, headers);  
			String responseFromPython = restTemplate.postForObject(pythonServerUrl, requestEntity, String.class);

            // Use the FHIR parser to deserialize the JSON response in a Bundle object
            Bundle bundleReturned = fhirContext.newJsonParser().parseResource(Bundle.class, responseFromPython);

            System.out.println("Risultato da Python: " + bundleReturned);

            return bundleReturned;
        } else {
            throw new ResourceNotFoundException(id);
        }
    }

    @Create
    public MethodOutcome createBundle(@ResourceParam Bundle bundle) {
    	
        // Generate a unique ID for the Bundle
    	Bundle bundle1 = dataGeneratorService.generateFHIRBundle();
    	
    	bundle = bundle1;
    	
    	bundle.setId(createId("Bundle", bundleCounter, 1L));
        bundles.put(String.valueOf(bundleCounter), bundle);
        bundleCounter++;

        // Return a positive result with the generated ID
        return new MethodOutcome(bundle.getIdElement());
    }
    
    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }

    /**
     * Method to create a specific ID for an FHIR resource
     */
    private static IdType createId(final String resourceType, final Long id, final Long versionId) {
        return new IdType(resourceType, "" + id, "" + versionId);
    }
}
