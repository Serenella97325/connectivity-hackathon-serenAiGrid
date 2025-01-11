package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
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
//public class BundleProvider implements IResourceProvider {
//
//    @Autowired
//    private BundleRepository fhirBundleRepository; // Repository per gestire i bundle nel database
//
//    @Override
//    public Class<? extends Resource> getResourceType() {
//        return Bundle.class; // Questo provider gestisce la risorsa Bundle
//    }
//
//    @Create
//    public MethodOutcome createBundle(@ResourceParam Bundle bundle) {
//  
//    	// Logica per creare una nuova osservazione nel database o in un repository
//        fhirBundleRepository.save(bundle);
//        return new MethodOutcome().setCreated(true).setResource(bundle);
//    }
//
//    @Read
//    public Optional<Bundle> read(IdType theId) {
//		// Logica per leggere un bundle dal database o repository
//		return fhirBundleRepository.findById(theId.getIdPart());
//    }
//
//    
//	@Search
//	public List<Observation> searchBundles(@OptionalParam(name = Bundle.SP_IDENTIFIER) TokenParam identifier) {
//		// Logica per cercare osservazioni basate su criteri
//		return fhirBundleRepository.search(identifier.getValue());
//	}
//
//    @Delete
//    public void deleteBundle(@IdParam String id) {
//        // Elimina il bundle dal database
//        fhirBundleRepository.deleteById(id);
//    }
//}

@Component
public class BundleProvider implements IResourceProvider {

    @Autowired
    private FhirContext fhirContext; // FhirContext per l'interazione con il server FHIR

    private final String serverBase = "http://localhost:8080"; // URL del server FHIR

    @Override
    public Class<? extends Resource> getResourceType() {
        return Bundle.class; // Questo provider gestisce la risorsa Bundle
    }

    @Create
    public MethodOutcome createBundle(@ResourceParam Bundle bundle) {
        // Usa il client FHIR per creare un nuovo bundle nel server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var outcome = client.create().resource(bundle).execute();

        return new MethodOutcome()
                .setCreated(true)
                .setResource(outcome.getResource());
    }

    @Read
    public Bundle read(@IdParam IdType theId) {
        // Usa il client FHIR per leggere un bundle dal server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        return client.read().resource(Bundle.class).withId(theId).execute();
    }

    @Search
    public List<Bundle> searchBundles(@OptionalParam(name = Bundle.SP_IDENTIFIER) TokenParam identifier) {
        // Usa il client FHIR per cercare bundles
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var bundle = client.search()
                .forResource(Bundle.class)
                .where(Bundle.IDENTIFIER.exactly().code(identifier.getValue()))
                .returnBundle(Bundle.class)
                .execute();

        return bundle.getEntry().stream()
                .map(entry -> (Bundle) entry.getResource())
                .collect(Collectors.toList());
    }

    @Delete
    public void deleteBundle(@IdParam IdType theId) {
        // Usa il client FHIR per eliminare un bundle
        var client = fhirContext.newRestfulGenericClient(serverBase);
        client.delete().resourceById(new IdType("Bundle", theId.getIdPart())).execute();
    }
}

