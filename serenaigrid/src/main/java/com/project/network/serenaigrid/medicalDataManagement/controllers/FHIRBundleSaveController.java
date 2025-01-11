package com.project.network.serenaigrid.medicalDataManagement.controllers;

import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.network.serenaigrid.medicalDataManagement.services.FHIRBundleSaveService;
import com.project.network.serenaigrid.utils.ApiResponse;

@RestController
@RequestMapping("/fhir")
public class FHIRBundleSaveController {
	
    @Autowired
    private FHIRBundleSaveService fhirBundleSaveService;
    
    @GetMapping("/Bundle/generate")
    public ResponseEntity<ApiResponse<Bundle>> generateAndSaveFHIRBundle() {
        // Chiama il servizio
        ApiResponse<Bundle> response = fhirBundleSaveService.generateAndSaveFHIRBundle();

        // Determina lo stato HTTP in base al successo o al fallimento
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(response);
    }
}
