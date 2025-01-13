package com.project.medicalDataManagement.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class ObservationProvider implements IResourceProvider {

    private static Long observationCounter = 1L;

    private static final ConcurrentHashMap<String, Observation> observations = new ConcurrentHashMap<>();

    @Read
    public Observation findObservation(@IdParam final IdType id) {
        if (observations.containsKey(id.getIdPart())) {
            return observations.get(id.getIdPart());
        } else {
            throw new ResourceNotFoundException(id);
        }
    }
    
    @Create
    public MethodOutcome createObservation(@ResourceParam Observation observation) {
 
        // Assegna un ID al Bundle
    	observation.setId(createId(observationCounter, 1L));
    	observations.put(String.valueOf(observationCounter), observation);
    	observationCounter++;

        return new MethodOutcome(observation.getIdElement());
    }
    
    @Override
    public Class<Observation> getResourceType() {
        return Observation.class;
    }
    
    private static IdType createId(final Long id, final Long versionId) {
        return new IdType("Observation", "" + id, "" + versionId);
    }
}

