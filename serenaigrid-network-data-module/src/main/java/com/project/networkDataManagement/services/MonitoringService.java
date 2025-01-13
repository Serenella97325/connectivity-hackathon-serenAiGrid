package com.project.networkDataManagement.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.networkDataManagement.models.MonitoringDataDO;
import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.services.exceptions.NetworkNotFoundException;
import com.project.networkDataManagement.services.exceptions.NetworkSimulatorException;
import com.project.networkDataManagement.utils.ApiResponse;

@Service
public class MonitoringService {

	private final NetworkRepository networkRepository;
	
    private final NetworkSimulatorService simulatorService;

    @Autowired
    public MonitoringService(NetworkSimulatorService simulatorService, NetworkRepository networkRepository) {
        this.simulatorService = simulatorService;
        this.networkRepository = networkRepository;
    }

	public ApiResponse<MonitoringDataDO> collectMonitoringData(String networkId) {
		try {

			// Simulates network data
			List<NetworkDetailsDO> simulatedData = simulatorService.simulateNetwork(networkId);
			if (simulatedData == null || simulatedData.isEmpty()) {
				throw new NetworkSimulatorException("Simulation failed");
			}
			
			// Get the details of network
			NetworkDO network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found"));

			// Combine and return data
			MonitoringDataDO monitoringData = new MonitoringDataDO(network, simulatedData, LocalDateTime.now());
			return ApiResponse.success(monitoringData);

		} catch (NetworkNotFoundException ex) {
			return ApiResponse.failure(ex.getMessage());
		} catch (NetworkSimulatorException ex) {
			return ApiResponse.failure(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.failure("Unexpected error: " + ex.getMessage());
		}
	}
}
