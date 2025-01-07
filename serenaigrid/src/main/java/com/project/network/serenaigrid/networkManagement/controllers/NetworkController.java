package com.project.network.serenaigrid.networkManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.network.serenaigrid.networkManagement.dtos.ApiResponse;
import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.services.NetworkService;

@RestController
@RequestMapping("/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;
    
    // Metodo per registrare una rete
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Network>> registerNetwork(@RequestBody Network network) {
        ApiResponse<Network> response = networkService.registerNetwork(network);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
    // Metodo per ottenere la lista di tutte le reti
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Network>>> listNetworks() {
        ApiResponse<List<Network>> response = networkService.getAllNetworks();
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
    // Metodo per ottenere una rete per ID
    @GetMapping("/network/{networkId}")
    public ResponseEntity<ApiResponse<Network>> getNetworkById(@PathVariable("networkId") String networkId) {
        ApiResponse<Network> response = networkService.getNetworkById(networkId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
}
