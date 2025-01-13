package com.project.networkDataManagement.controllers;

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

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.services.NetworkService;
import com.project.networkDataManagement.utils.ApiResponse;

@RestController
@RequestMapping("/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;
    
    // Method for registering a network
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<NetworkDO>> registerNetwork(@RequestBody NetworkDO network) {
        ApiResponse<NetworkDO> response = networkService.registerNetwork(network);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
    // Method for obtaining the list of all networks
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<NetworkDO>>> listNetworks() {
        ApiResponse<List<NetworkDO>> response = networkService.getAllNetworks();
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
    // Method to get a network by ID
    @GetMapping("/network/{networkId}")
    public ResponseEntity<ApiResponse<NetworkDO>> getNetworkById(@PathVariable("networkId") String networkId) {
        ApiResponse<NetworkDO> response = networkService.getNetworkById(networkId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
}
