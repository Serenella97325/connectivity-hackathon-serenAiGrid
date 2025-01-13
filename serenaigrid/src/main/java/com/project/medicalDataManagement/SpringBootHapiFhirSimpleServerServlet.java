package com.project.medicalDataManagement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.project.medicalDataManagement.providers.BundleProvider;
import com.project.medicalDataManagement.providers.DeviceProvider;
import com.project.medicalDataManagement.providers.PatientProvider;
import com.project.medicalDataManagement.services.FHIRDataGeneratorService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", loadOnStartup = 1) 
public class SpringBootHapiFhirSimpleServerServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    private boolean serverStarted = false;
    
    private final FHIRDataGeneratorService dataGeneratorService;
    private final RestTemplate restTemplate;
    private final FhirContext fhirContext;

    @Autowired
    public SpringBootHapiFhirSimpleServerServlet(FHIRDataGeneratorService dataGeneratorService, RestTemplate restTemplate, FhirContext fhirContext) {
        this.dataGeneratorService = dataGeneratorService;
        this.restTemplate = restTemplate;
        this.fhirContext = fhirContext;
    }

    @Override
    public void initialize() {
        if (!serverStarted) {
            /*
             Add Resource Providers
             */
            List<IResourceProvider> providers = new ArrayList<>();
            providers.add(new PatientProvider());
            providers.add(new BundleProvider(dataGeneratorService, restTemplate, fhirContext));
            providers.add(new DeviceProvider());
            setResourceProviders(providers);

            /*
            Various config
             */
            setDefaultResponseEncoding(EncodingEnum.JSON);
            setImplementationDescription("Spring Boot HAPI-FHIR (R4) Simple Server");
            setDefaultPrettyPrint(true);
            
            serverStarted = true;
        }
    }
}

