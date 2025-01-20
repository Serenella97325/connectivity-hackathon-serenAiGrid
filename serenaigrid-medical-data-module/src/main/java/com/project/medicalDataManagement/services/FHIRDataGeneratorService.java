package com.project.medicalDataManagement.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
public class FHIRDataGeneratorService {

    private static final Random random = new Random();

    Date now = new Date(System.currentTimeMillis());  // Get current time with milliseconds
    
    // Mappa per tipi di emergenze e codici associati
    private static final Map<String, String> EMERGENCY_MAP = new HashMap<>();
    static {
        EMERGENCY_MAP.put("Cardiac arrest event", "LA19926-4");
        EMERGENCY_MAP.put("Severe allergic reaction", "LA10317-0");
        EMERGENCY_MAP.put("Stroke event", "LA14546-8");
        EMERGENCY_MAP.put("Traumatic injury", "LA24090-6");
        EMERGENCY_MAP.put("Respiratory distress", "LA19875-5");
    }

    /**
     * Generate medical emergencies with FHIR data and network extensions
     */
    public List<Observation> generateEmergencies() {
        List<Observation> emergencies = new ArrayList<>();
        int numEmergencies = random.nextInt(4) + 1; // Up to 5 emergencies

        for (int i = 0; i < numEmergencies; i++) {
            Observation emergency = new Observation();
            emergency.setId(UUID.randomUUID().toString());
            emergency.setStatus(Observation.ObservationStatus.FINAL);

            // Type of random emergency
            String emergencyType = getRandomEmergencyType();
            String emergencyCode = EMERGENCY_MAP.get(emergencyType);

            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem("http://loinc.org")
                .setCode(emergencyCode)
                .setDisplay(emergencyType);
            emergency.setCode(codeableConcept);

            emergency.setSubject(new Reference("Patient/" + (i + 1)));
            emergency.setEffective(new DateTimeType(now));
            emergency.addNote().setText("Emergency #" + (i + 1) + ": " + emergencyType);
            
            // Generate random values for latency and bandwidth
            double latency = random.nextDouble() * 200; // Latenza in ms
            double bandwidth = random.nextDouble() * 100; // Banda in Mbps

            // Extensions for the network
            emergency.addExtension(createNetworkExtension("http://emergency.org/fhir/network-priority", "High"));
            emergency.addExtension(createNetworkExtension("http://emergency.org/fhir/network-latency", latency, "ms"));
            emergency.addExtension(createNetworkExtension("http://emergency.org/fhir/network-bandwidth", bandwidth, "Mbps"));
            
            // Quality of Service (QoS) 
            CodeableConcept qosCode = new CodeableConcept();
            qosCode.addCoding()
                .setSystem("http://emergency.org/fhir/network-qos")
                .setCode("emergency-qos")
                .setDisplay("Emergency QoS Metrics");
            emergency.addComponent()
                .setCode(qosCode)
                .setValue(new Quantity(bandwidth / latency).setUnit("QoS Unit")); // QoS = Banda / Latenza
            
            emergencies.add(emergency);
        }

        return emergencies;
    }

    /**
     * Generate telemedicine sessions with FHIR QoS and network extensions
     */
    public List<Observation> generateTelemedicineSessions() {
    	List<Observation> telemedicineSessions = new ArrayList<>();
    	int numTelemedicineSessions = random.nextInt(5) + 1; // Up to 5  telemedicine sessions
    	
    	for (int i = 0; i < numTelemedicineSessions; i++) {
            Observation telemedicine = new Observation();
            telemedicine.setId(UUID.randomUUID().toString());
            telemedicine.setStatus(Observation.ObservationStatus.FINAL);
            
            telemedicine.setEffective(new DateTimeType(now));
            
            // Generate random values for latency and bandwidth
            double latency = random.nextDouble() * 200; // Latenza in ms
            double bandwidth = random.nextDouble() * 100; // Banda in Mbps
            
            // Generate random priority ("Normal" or "Low")
            String networkPriority = random.nextBoolean() ? "Normal" : "Low";
            
            // Extensions for the network
            telemedicine.addExtension(createNetworkExtension("http://telemedicine.org/fhir/network-priority", networkPriority));
            telemedicine.addExtension(createNetworkExtension("http://telemedicine.org/fhir/network-latency", latency, "ms"));
            telemedicine.addExtension(createNetworkExtension("http://telemedicine.org/fhir/network-bandwidth", bandwidth, "Mbps"));
            
            // Quality of Service (QoS) 
            CodeableConcept qosCode = new CodeableConcept();
            qosCode.addCoding()
                .setSystem("http://telemedicine.org/fhir/network-qos")
                .setCode("telemedicine-qos")
                .setDisplay("Telemedicine QoS Metrics");
            telemedicine.addComponent()
                .setCode(qosCode)
                .setValue(new Quantity(bandwidth / latency).setUnit("QoS Unit")); // QoS = Banda / Latenza
            
            telemedicineSessions.add(telemedicine);    		
    	}
    	
        return telemedicineSessions;
    }

    /**
     * Generate data on medical devices
     */
    public List<Device> generateDevices() {
        String[] deviceTypes = {"Ventilator", "ECG Monitor", "Defibrillator"};
        List<Device> devices = new ArrayList<>();

        for (String type : deviceTypes) {
            Device device = new Device();
            device.setId(UUID.randomUUID().toString());
            device.getType().addCoding()
                .setSystem("http://snomed.info/sct")
                .setDisplay(type);

            // Randomly assign device status as ACTIVE or INACTIVE
            Device.FHIRDeviceStatus status = random.nextBoolean() ? Device.FHIRDeviceStatus.ACTIVE : Device.FHIRDeviceStatus.INACTIVE;
            device.setStatus(status);
            
            Device.DevicePropertyComponent property = device.addProperty();
            Quantity quantity = new Quantity(random.nextInt(10)).setUnit("units");
            property.addValueQuantity(quantity);

            // Add network properties (priority, latency, bandwidth) only if the device is ACTIVE
            if (status == Device.FHIRDeviceStatus.ACTIVE) {
                // Generate random values for network priority, latency, and bandwidth
                String networkPriority = "High";  
                double latency = random.nextDouble() * 200; // Latency in ms
                double bandwidth = random.nextDouble() * 100; // Bandwidth in Mbps
                double qos = bandwidth / latency;

                // Extensions for the network
                device.addExtension(createNetworkExtension("http://device.org/fhir/network-priority", networkPriority));
                device.addExtension(createNetworkExtension("http://device.org/fhir/network-latency", latency, "ms"));
                device.addExtension(createNetworkExtension("http://device.org/fhir/network-bandwidth", bandwidth, "Mbps"));
                device.addExtension(createNetworkExtension("http://device.org/fhir/network-qos", qos, "QoS Unit"));

            }

            devices.add(device);
        }

        return devices;
    }

    /**
     * Generate a bundle that includes emergencies, telemedicine and devices
     */
    public Bundle generateFHIRBundle() {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        
        // Set a valid value for lastUpdated
        Meta meta = new Meta();
        meta.setLastUpdated(new Date()); // Set current date and time
        bundle.setMeta(meta);

        // Add emergencies
        generateEmergencies().forEach(emergency ->
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(emergency))
        );

        // Add telemedicine session        
		generateTelemedicineSessions().forEach(telemedicine -> 
		    bundle.addEntry(new Bundle.BundleEntryComponent().setResource(telemedicine)));

        // Add devices
        generateDevices().forEach(device ->
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(device))
        );

        return bundle;
    }

    /**
     * Create a FHIR extension with value String
     */
    private Extension createNetworkExtension(String url, String value) {
        return new Extension(url, new StringType(value));
    }

    /**
     * Create a FHIR extension with value Quantity
     */
    private Extension createNetworkExtension(String url, double value, String unit) {
        Quantity quantity = new Quantity();
        quantity.setValue(value).setUnit(unit);
        return new Extension(url, quantity);
    }

    /**
     * Returns a random emergency type
     */
    private String getRandomEmergencyType() {
        List<String> keys = new ArrayList<>(EMERGENCY_MAP.keySet());
        return keys.get(random.nextInt(keys.size()));
    }
}