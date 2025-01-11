package com.project.network.serenaigrid.medicalDataManagement.configs;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;

import com.project.network.serenaigrid.medicalDataManagement.providers.BundleProvider;
import com.project.network.serenaigrid.medicalDataManagement.providers.DeviceProvider;
import com.project.network.serenaigrid.medicalDataManagement.providers.ObservationProvider;
import com.project.network.serenaigrid.medicalDataManagement.providers.PatientProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/*")
public class FhirRestfulServer extends RestfulServer {

	private static final long serialVersionUID = 1L;
	private ApplicationContext applicationContext;

	public FhirRestfulServer(ApplicationContext context) {

		this.applicationContext = context;
	}

	@Override
	protected void initialize() throws ServletException {
		super.initialize();
		setFhirContext(FhirContext.forR4());
		setProviders(applicationContext.getBean(PatientProvider.class), // Provider per Patient
				applicationContext.getBean(ObservationProvider.class), // Provider per Observation
				applicationContext.getBean(DeviceProvider.class), // Provider per Device
				applicationContext.getBean(BundleProvider.class) // Provider per Bundle

		);
	}
}
