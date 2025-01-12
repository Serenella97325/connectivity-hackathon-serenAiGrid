package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class DeviceProvider implements IResourceProvider {
	
	private static Long deviceCounter = 1L;
	
    private static final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>();

    @Read
    public Device findDevice(@IdParam final IdType id) {
        if (devices.containsKey(id.getIdPart())) {
            return devices.get(id.getIdPart());
        } else {
            throw new ResourceNotFoundException(id);
        }
    }
    
    @Create
    public MethodOutcome createDevice(@ResourceParam Device device) {
        
    	// Assegna un ID al device
    	device.setId(createId(deviceCounter, 1L));
    	devices.put(String.valueOf(deviceCounter), device);
    	deviceCounter++;

        return new MethodOutcome(device.getIdElement());
    }
    
    @Override
    public Class<Device> getResourceType() {
        return Device.class;
    }
    
    private static IdType createId(final Long id, final Long versionId) {
        return new IdType("Device", "" + id, "" + versionId);
    }
}





