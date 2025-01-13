package com.project.networkDataManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.services.NetworkSimulatorService;

@RestController
@RequestMapping("/network-simulator")
public class NetworkSimulatorController {

    @Autowired
    private NetworkSimulatorService simulatorService;

    @GetMapping("/simulate/{networkId}")
    public ResponseEntity<List<NetworkDetailsDO>> simulateNetwork(@PathVariable("networkId") String networkId) {
        
    	// Call to service to simulate the network
        List<NetworkDetailsDO> response = simulatorService.simulateNetwork(networkId);
        
        // Check if the simulation was successful
        if (!response.isEmpty()) {
            // If the simulation is successful, return the answer with OK code (200)
            return ResponseEntity.ok(response);
        } else {
            // If there was an error in the simulation, return INTERNAL_SERVER_ERROR (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
