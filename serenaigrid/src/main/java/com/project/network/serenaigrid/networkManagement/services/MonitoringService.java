package com.project.network.serenaigrid.networkManagement.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.network.serenaigrid.networkManagement.dtos.ApiResponse;
import com.project.network.serenaigrid.networkManagement.models.MonitoringData;
import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetails;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkNotFoundException;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkSimulatorException;

@Service
public class MonitoringService {

	@Autowired
	private NetworkRepository networkRepository;

	@Autowired
	private NetworkSimulatorService simulatorService;

	public ApiResponse<MonitoringData> collectMonitoringData(String networkId) {
		try {
			// Ottieni i dettagli della rete
			Network network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found"));

			// Simula i dati di rete
			List<NetworkDetails> simulatedData = simulatorService.simulateNetwork(networkId);
			if (simulatedData == null || simulatedData.isEmpty()) {
				throw new NetworkSimulatorException("Simulation failed");
			}

			// Combina e restituisci i dati
			MonitoringData monitoringData = new MonitoringData(network, simulatedData, LocalDateTime.now());
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
