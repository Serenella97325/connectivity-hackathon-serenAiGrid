package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

//@Component
//public class ObservationProvider implements IResourceProvider {
//
//	@Autowired
//	private ObservationRepository observationRepository; // Il repository per l'interazione con il database
//
//	@Override
//	public Class<? extends IBaseResource> getResourceType() {
//		return Observation.class;
//	}
//
//	@Create
//	public MethodOutcome createObservation(@ResourceParam Observation observation) {
//		// Logica per creare una nuova osservazione nel database o in un repository
//		observationRepository.save(observation);
//		return new MethodOutcome().setCreated(true).setResource(observation);
//	}
//
//	@Read
//	public Optional<Observation> read(@IdParam IdType theId) {
//		// Logica per leggere un'osservazione dal database o repository
//		return observationRepository.findById(theId.getIdPart());
//	}
//
//	@Search
//	public List<Observation> searchObservations(@OptionalParam(name = Observation.SP_IDENTIFIER) TokenParam identifier) {
//		// Logica per cercare osservazioni basate su criteri
//		return observationRepository.search(identifier.getValue());
//	}
//	
//    @Delete
//    public void deleteObservations(@IdParam String id) {
//        // Elimina un'osservazione dal database
//    	observationRepository.deleteById(id);
//    }
//}

@Component
public class ObservationProvider implements IResourceProvider {

    @Autowired
    private FhirContext fhirContext;

    private final String serverBase = "http://localhost:8080"; // URL del server FHIR

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Create
    public MethodOutcome createObservation(@ResourceParam Observation observation) {
        // Usa il client FHIR per creare l'osservazione nel server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var outcome = client.create().resource(observation).execute();

        return new MethodOutcome()
                .setCreated(true)
                .setResource(outcome.getResource());
    }

    @Read
    public Observation read(@IdParam IdType theId) {
        // Usa il client FHIR per leggere un'osservazione dal server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        return client.read().resource(Observation.class).withId(theId).execute();
    }

    @Search
    public List<Observation> searchObservations(@OptionalParam(name = Observation.SP_IDENTIFIER) TokenParam identifier) {
        // Usa il client FHIR per cercare osservazioni
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var bundle = client.search()
                .forResource(Observation.class)
                .where(Observation.IDENTIFIER.exactly().code(identifier.getValue()))
                .returnBundle(Bundle.class)
                .execute();

        return bundle.getEntry().stream()
                .map(entry -> (Observation) entry.getResource())
                .collect(Collectors.toList());
    }

    @Delete
    public void deleteObservations(@IdParam IdType theId) {
        // Usa il client FHIR per eliminare un'osservazione
        var client = fhirContext.newRestfulGenericClient(serverBase);
        client.delete().resourceById(new IdType("Observation", theId.getIdPart())).execute();
    }
}

