package com.project.network.serenaigrid.medicalDataManagement.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class PatientDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // Metodo per cercare pazienti con diversi parametri (filtro per data di nascita, cognome, genere, etc.)
    @Transactional
    public List<Patient> search(FhirContext ctx, DateRangeParam birthDate, StringParam familyName,
                                StringParam gender, StringParam givenName, TokenParam identifier,
                                StringParam name) {

        StringBuilder queryString = new StringBuilder("SELECT p FROM Patient p WHERE 1=1");

        if (birthDate != null) {
            queryString.append(" AND p.birthDate BETWEEN :startDate AND :endDate");
        }
        if (familyName != null) {
            queryString.append(" AND p.familyName = :familyName");
        }
        if (gender != null) {
            queryString.append(" AND p.gender = :gender");
        }
        if (givenName != null) {
            queryString.append(" AND p.givenName = :givenName");
        }
        if (identifier != null) {
            queryString.append(" AND p.identifier = :identifier");
        }
        if (name != null) {
            queryString.append(" AND p.name = :name");
        }

        Query query = entityManager.createQuery(queryString.toString(), Patient.class);

        // Impostare i parametri di ricerca
        if (birthDate != null) {
            query.setParameter("startDate", birthDate.getLowerBoundAsDateInteger());
            query.setParameter("endDate", birthDate.getUpperBoundAsDateInteger());
        }
        if (familyName != null) {
            query.setParameter("familyName", familyName.getValue());
        }
        if (gender != null) {
            query.setParameter("gender", gender.getValue());
        }
        if (givenName != null) {
            query.setParameter("givenName", givenName.getValue());
        }
        if (identifier != null) {
            query.setParameter("identifier", identifier.getValue());
        }
        if (name != null) {
            query.setParameter("name", name.getValue());
        }

        return query.getResultList();
    }

    // Metodo per leggere un paziente da database tramite il suo ID
    @Transactional
    public Patient read(FhirContext ctx, IdType theId) {
        return entityManager.find(Patient.class, theId.getIdPart());
    }

    // Metodo per creare un nuovo paziente
    @Transactional
    public MethodOutcome create(FhirContext ctx, Patient patient) {
        entityManager.persist(patient);  // Persisti il paziente nel database
        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setId(new IdType("Patient", patient.getIdElement().getIdPart())); // ID del paziente appena creato
        return outcome;
    }

    // Metodo per ottenere tutti i pazienti (senza filtri)
    @Transactional
    public List<Patient> getAllPatients() {
        return entityManager.createQuery("SELECT p FROM Patient p", Patient.class).getResultList();
    }

    // Metodo per eliminare un paziente dal database
    @Transactional
    public void delete(FhirContext ctx, IdType theId) {
        Patient patient = entityManager.find(Patient.class, theId.getIdPart());
        if (patient != null) {
            entityManager.remove(patient); // Rimuovi il paziente dal database
        }
    }
}
