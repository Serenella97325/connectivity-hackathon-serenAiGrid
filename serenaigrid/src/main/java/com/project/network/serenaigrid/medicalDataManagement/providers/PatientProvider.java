package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

    @Component
    public class PatientProvider implements IResourceProvider {

        private static Long counter = 1L;

        private static final ConcurrentHashMap<String, Patient> patients = new ConcurrentHashMap<>();

        static {
            patients.put(String.valueOf(counter), createPatient("Stitt"));
            patients.put(String.valueOf(counter), createPatient("Hodges"));
            patients.put(String.valueOf(counter), createPatient("Desmond"));
        }


        @Read
        public Patient find(@IdParam final IdType id) {
            if (patients.containsKey(id.getIdPart())) {
                return patients.get(id.getIdPart());
            } else {
                throw new ResourceNotFoundException(id);
            }
        }

        @Create
        public MethodOutcome createPatient(@ResourceParam Patient patient) {

            patient.setId(createId(counter, 1L));
            patients.put(String.valueOf(counter), patient);

            return new MethodOutcome(patient.getIdElement());
        }

        @Override
        public Class<Patient> getResourceType() {
            return Patient.class;
        }

        private static IdType createId(final Long id, final Long versionId) {
            return new IdType("Patient", "" + id, "" + versionId);
        }

        private static Patient createPatient(final String name) {
            final Patient patient = new Patient();
            patient.getName().add(new HumanName().setFamily(name));
            patient.setId(createId(counter, 1L));
            counter++;
            return patient;
        }


}