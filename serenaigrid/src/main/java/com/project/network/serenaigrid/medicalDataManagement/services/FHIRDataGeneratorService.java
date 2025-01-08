package com.project.network.serenaigrid.medicalDataManagement.services;

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
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
public class FHIRDataGeneratorService {

    private static final Random random = new Random();

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
     * Genera emergenze mediche con dati FHIR e estensioni per rete
     */
    public List<Observation> generateEmergencies() {
        List<Observation> emergencies = new ArrayList<>();
        int numEmergencies = random.nextInt(5) + 1; // Fino a 5 emergenze

        for (int i = 0; i < numEmergencies; i++) {
            Observation emergency = new Observation();
            emergency.setId(UUID.randomUUID().toString());
            emergency.setStatus(Observation.ObservationStatus.FINAL);

            // Tipo di emergenza casuale
            String emergencyType = getRandomEmergencyType();
            String emergencyCode = EMERGENCY_MAP.get(emergencyType);

            // Imposta correttamente il CodeableConcept
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem("http://loinc.org")
                .setCode(emergencyCode)
                .setDisplay(emergencyType);
            emergency.setCode(codeableConcept);

            emergency.setSubject(new Reference("Patient/" + (i + 1)));
            emergency.setEffective(new DateTimeType(new Date()));
            emergency.addNote().setText("Emergency #" + (i + 1) + ": " + emergencyType);

            // Estensioni per la rete
            emergency.addExtension(createNetworkExtension("http://example.org/fhir/network-priority", "High"));
            emergency.addExtension(createNetworkExtension("http://example.org/fhir/network-latency", random.nextDouble() * 200, "ms"));
            
            emergencies.add(emergency);
        }

        return emergencies;
    }

    /**
     * Genera sessioni di telemedicina con QoS FHIR e estensioni di rete
     */
    public Observation generateTelemedicineSession() {
        Observation telemedicine = new Observation();
        telemedicine.setId(UUID.randomUUID().toString());
        telemedicine.setStatus(Observation.ObservationStatus.FINAL);
        
        // Imposta correttamente il CodeableConcept
        CodeableConcept qosCode = new CodeableConcept();
        qosCode.addCoding()
            .setSystem("http://example.org/fhir/qos")
            .setCode("telemed-qos")
            .setDisplay("Telemedicine QoS Metrics");     
        telemedicine.setCode(qosCode); 

        // For "Latency", wrap Coding into CodeableConcept
        CodeableConcept latencyCode = new CodeableConcept();
        latencyCode.addCoding()
            .setDisplay("Latency");
        telemedicine.addComponent()
            .setCode(latencyCode)  
            .setValue(new Quantity(random.nextDouble() * 200).setUnit("ms"));

        // For "Bandwidth Usage", wrap Coding into CodeableConcept
        CodeableConcept bandwidthCode = new CodeableConcept();
        bandwidthCode.addCoding()
            .setDisplay("Bandwidth Usage");
        telemedicine.addComponent()
            .setCode(bandwidthCode)  
            .setValue(new Quantity(random.nextDouble() * 100).setUnit("Mbps"));
        
        telemedicine.setEffective(new DateTimeType(new Date()));

        // Estensioni di rete
        telemedicine.addExtension(createNetworkExtension("http://example.org/fhir/telemedicine-priority", "Normal"));

        return telemedicine;
    }

    /**
     * Genera dati sui dispositivi medici
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

            devices.add(device);
        }

        return devices;
    }

    /**
     * Genera un bundle che include emergenze, telemedicina e dispositivi
     */
    public Bundle generateFHIRBundle() {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);

        // Aggiungi emergenze
        generateEmergencies().forEach(emergency ->
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(emergency))
        );

        // Aggiungi sessione di telemedicina
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(generateTelemedicineSession()));

        // Aggiungi dispositivi
        generateDevices().forEach(device ->
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(device))
        );

        return bundle;
    }

    /**
     * Crea un'estensione FHIR con valore String
     */
    private Extension createNetworkExtension(String url, String value) {
        return new Extension(url, new StringType(value));
    }

    /**
     * Crea un'estensione FHIR con valore Quantity
     */
    private Extension createNetworkExtension(String url, double value, String unit) {
        Quantity quantity = new Quantity();
        quantity.setValue(value).setUnit(unit);
        return new Extension(url, quantity);
    }

    /**
     * Restituisce un tipo di emergenza casuale
     */
    private String getRandomEmergencyType() {
        List<String> keys = new ArrayList<>(EMERGENCY_MAP.keySet());
        return keys.get(random.nextInt(keys.size()));
    }
}