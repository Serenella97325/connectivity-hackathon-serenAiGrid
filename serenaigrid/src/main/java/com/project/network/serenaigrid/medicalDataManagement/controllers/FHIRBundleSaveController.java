package com.project.network.serenaigrid.medicalDataManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.network.serenaigrid.medicalDataManagement.models.FHIRBundleDO;
import com.project.network.serenaigrid.medicalDataManagement.services.FHIRBundleSaveService;
import com.project.network.serenaigrid.utils.ApiResponse;

@RestController
@RequestMapping("/fhir-bundle")
public class FHIRBundleSaveController {
	
    @Autowired
    private FHIRBundleSaveService fhirBundleSaveService;
    
    @GetMapping("/generate")
    public ResponseEntity<ApiResponse<FHIRBundleDO>> generateAndSaveFHIRBundle() {
        
        // Chiama il servizio e ottiene un'ApiResponse
        ApiResponse<FHIRBundleDO> response = fhirBundleSaveService.generateAndSaveFHIRBundle();
        
        // Restituisce lo stato HTTP in base al successo o al fallimento
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
