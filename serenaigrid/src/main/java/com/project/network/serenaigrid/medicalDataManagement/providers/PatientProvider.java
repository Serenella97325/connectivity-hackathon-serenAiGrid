package com.project.network.serenaigrid.medicalDataManagement.providers;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.network.serenaigrid.medicalDataManagement.dao.PatientDAO;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class PatientProvider implements IResourceProvider {

	@Autowired
	private FhirContext ctx;

	@Autowired
	private PatientDAO patientDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Patient.class;
	}

	@Search
	public List<Patient> searchPatient(HttpServletRequest request,
			@OptionalParam(name = Patient.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = Patient.SP_FAMILY) StringParam familyName,
			@OptionalParam(name = Patient.SP_GENDER) StringParam gender,
			@OptionalParam(name = Patient.SP_GIVEN) StringParam givenName,
			@OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = Patient.SP_NAME) StringParam name,
			@OptionalParam(name = Patient.SP_RES_ID) TokenParam resid) {

		return patientDao.search(ctx, birthDate, familyName, gender, givenName, identifier, name);
	}

	@Read()
	public Patient read(@IdParam IdType theId) {

		return patientDao.read(ctx, theId);

	}

	@Create()
    public MethodOutcome createPatient(HttpServletRequest theRequest, @ResourceParam Patient patient) {

        MethodOutcome  method = new  MethodOutcome();
        method.setCreated(true);
        OperationOutcome  opOutcome = new  OperationOutcome();
        method.setOperationOutcome(opOutcome);
       
        return patientDao.create(ctx, patient);
    }

	@Search()
	public List<Patient> getAllPatients() {
		return patientDao.getAllPatients();

	}

	@Delete()
	public void delete(@IdParam IdType theId) {

		patientDao.delete(ctx, theId);
	}
}
