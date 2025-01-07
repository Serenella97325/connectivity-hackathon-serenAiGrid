package com.project.network.serenaigrid.networkManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.network.serenaigrid.networkManagement.dtos.ApiResponse;
import com.project.network.serenaigrid.networkManagement.models.MonitoringData;
import com.project.network.serenaigrid.networkManagement.services.MonitoringService;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/network/{networkId}")
    public ResponseEntity<ApiResponse<MonitoringData>> getNetworkMonitoring(@PathVariable("networkId") String networkId) {
    	
        // Il servizio gestisce le eccezioni e restituisce un'ApiResponse, quindi basta inoltrare la risposta
        ApiResponse<MonitoringData> response = monitoringService.collectMonitoringData(networkId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

