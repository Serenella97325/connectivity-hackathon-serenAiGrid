package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
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
//public class DeviceProvider implements IResourceProvider {
//
//	@Autowired
//	private DeviceRepository deviceRepository; // Il repository per l'interazione con il database
//
//	@Override
//	public Class<? extends IBaseResource> getResourceType() {
//		return Device.class;
//	}
//
//	@Create
//	public MethodOutcome createDevice(@ResourceParam Device device) {
//		// Logica per creare un nuovo dispositivo nel database o nel repository
//		deviceRepository.save(device);
//		return new MethodOutcome().setCreated(true).setResource(device);
//	}
//
//	@Read
//	public Optional<Device> read(@IdParam IdType theId) {
//		// Logica per leggere un dispositivo dal database o dal repository
//		return deviceRepository.findById(theId.getIdPart());
//	}
//
//	@Search
//	public List<Device> searchDevices(@OptionalParam(name = Device.SP_IDENTIFIER) TokenParam identifier) {
//		// Logica per cercare dispositivi basati su criteri, come l'ID del dispositivo
//		return deviceRepository.search(identifier.getValue());
//	}
//	
//    @Delete
//    public void deleteDevices(@IdParam String id) {
//        // Elimina un device dal database
//    	deviceRepository.deleteById(id);
//    }
//}

@Component
public class DeviceProvider implements IResourceProvider {

    @Autowired
    private FhirContext fhirContext;

    private final String serverBase = "http://localhost:8080/"; // Base URL del server FHIR

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Device.class;
    }

    @Create
    public MethodOutcome createDevice(@ResourceParam Device device) {
        // Usa il client FHIR per creare il device nel server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var outcome = client.create().resource(device).execute();

        return new MethodOutcome()
                .setCreated(true)
                .setResource(outcome.getResource());
    }

    @Read
    public Device read(@IdParam IdType theId) {
        // Usa il client FHIR per leggere un device dal server
        var client = fhirContext.newRestfulGenericClient(serverBase);
        return client.read().resource(Device.class).withId(theId).execute();
    }

    @Search
    public List<Device> searchDevices(@OptionalParam(name = Device.SP_IDENTIFIER) TokenParam identifier) {
        // Usa il client FHIR per cercare dispositivi
        var client = fhirContext.newRestfulGenericClient(serverBase);
        var bundle = client.search()
                .forResource(Device.class)
                .where(Device.IDENTIFIER.exactly().code(identifier.getValue()))
                .returnBundle(Bundle.class)
                .execute();

        return bundle.getEntry().stream()
                .map(entry -> (Device) entry.getResource())
                .collect(Collectors.toList());
    }

    @Delete
    public void deleteDevices(@IdParam IdType theId) {
        // Usa il client FHIR per eliminare un dispositivo
        var client = fhirContext.newRestfulGenericClient(serverBase);
        client.delete().resourceById(new IdType("Device", theId.getIdPart())).execute();
    }
}


