package com.project.networkDataManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.networkDataManagement.models.MonitoringDataDO;
import com.project.networkDataManagement.services.MonitoringService;
import com.project.networkDataManagement.utils.ApiResponse;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

	private final MonitoringService monitoringService;
	private final RestTemplate restTemplate;

	@Autowired
	public MonitoringController(MonitoringService monitoringService, RestTemplate restTemplate) {
		this.monitoringService = monitoringService;
		this.restTemplate = restTemplate;
	}

//    @GetMapping("/network/{networkId}")
//    public ResponseEntity<ApiResponse<MonitoringDataDO>> getNetworkMonitoring(@PathVariable("networkId") String networkId) {
//    	
//        ApiResponse<MonitoringDataDO> response = monitoringService.collectMonitoringData(networkId);
//        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }

	@GetMapping("/network/{networkId}")
	public ResponseEntity<ApiResponse<MonitoringDataDO>> getNetworkMonitoring(@PathVariable("networkId") String networkId) {

		try {
			// Get the monitoring data
			MonitoringDataDO monitoringData = monitoringService.collectMonitoringData(networkId).getData();

			if (monitoringData == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("No monitoring network data found"));
			}

			// Send monitoring data to the Python server
			String pythonServerUrl = "http://localhost:5000/process-monitoring-data";

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");

			// Make the request to the Python server
			HttpEntity<MonitoringDataDO> requestEntity = new HttpEntity<>(monitoringData, headers);
			MonitoringDataDO networkReturned = restTemplate.postForObject(pythonServerUrl, requestEntity, MonitoringDataDO.class);

			System.out.println("Network Data result from Python: " + networkReturned);

			return ResponseEntity.ok(ApiResponse.success(networkReturned));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure("Error during processing: " + ex.getMessage()));
		}
	}
}
