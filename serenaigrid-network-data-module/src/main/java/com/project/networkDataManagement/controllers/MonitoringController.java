package com.project.networkDataManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.networkDataManagement.models.MonitoringDataDO;
import com.project.networkDataManagement.services.MonitoringService;
import com.project.networkDataManagement.utils.ApiResponse;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/network/{networkId}")
    public ResponseEntity<ApiResponse<MonitoringDataDO>> getNetworkMonitoring(@PathVariable("networkId") String networkId) {
    	
        ApiResponse<MonitoringDataDO> response = monitoringService.collectMonitoringData(networkId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

