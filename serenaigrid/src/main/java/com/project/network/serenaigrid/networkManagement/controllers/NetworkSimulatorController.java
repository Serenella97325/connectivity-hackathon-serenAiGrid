package com.project.network.serenaigrid.networkManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.network.serenaigrid.networkManagement.models.NetworkDetails;
import com.project.network.serenaigrid.networkManagement.services.NetworkSimulatorService;

@RestController
@RequestMapping("/network-simulator")
public class NetworkSimulatorController {

    @Autowired
    private NetworkSimulatorService simulatorService;

    @GetMapping("/simulate/{networkId}")
    public ResponseEntity<List<NetworkDetails>> simulateNetwork(@PathVariable("networkId") String networkId) {
        // Chiamata al servizio per simulare la rete
        List<NetworkDetails> response = simulatorService.simulateNetwork(networkId);
        
        // Verifica se la simulazione è andata a buon fine
        if (!response.isEmpty()) {
            // Se la simulazione è riuscita, restituisci la risposta con codice OK (200)
            return ResponseEntity.ok(response);
        } else {
            // Se c'è stato un errore nella simulazione, restituisci INTERNAL_SERVER_ERROR (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
