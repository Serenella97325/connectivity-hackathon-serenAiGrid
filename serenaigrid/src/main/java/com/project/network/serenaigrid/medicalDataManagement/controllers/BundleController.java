package com.project.network.serenaigrid.medicalDataManagement.controllers;

import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/bundle")
public class BundleController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<?> createBundleAndSend(@RequestBody Bundle bundle) {
        // Converti il bundle in formato JSON
        String bundleJson = convertBundleToJson(bundle);

        // URL del server Python
        String pythonServerUrl = "http://localhost:5000/process-bundle";

        // Invia il bundle JSON al server Python
        ResponseEntity<String> pythonResponse = restTemplate.postForEntity(pythonServerUrl, bundleJson, String.class);

        if (pythonResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(pythonResponse.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Errore nel processo del bundle sul server Python");
        }
    }

    // Metodo per convertire il Bundle in formato JSON
    private String convertBundleToJson(Bundle bundle) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(bundle);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";  // Restituisce un JSON vuoto in caso di errore
        }
    }
}
