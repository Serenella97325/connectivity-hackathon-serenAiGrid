package com.project.networkDataManagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.repositories.NetworkDetailsRepository;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.services.exceptions.NetworkNotFoundException;
import com.project.networkDataManagement.services.exceptions.NetworkSimulatorException;

@Service
public class NetworkSimulatorService {

    private final NetworkRepository networkRepository;
    private final NetworkDetailsRepository dataRepository;

    @Autowired
    public NetworkSimulatorService(NetworkRepository networkRepository, NetworkDetailsRepository dataRepository) {
        this.networkRepository = networkRepository;
        this.dataRepository = dataRepository;
    }

	@Transactional
	public List<NetworkDetailsDO> simulateNetwork(String networkId) {
		try {
			
			// Retrieves the Network object or throws an exception if not found
			NetworkDO network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found with ID: " + networkId));

			List<NetworkDetailsDO> simulatedData = new ArrayList<>();
			Random random = new Random();

			for (int i = 0; i < network.getNodeCount(); i++) {
				NetworkDetailsDO data = NetworkDetailsDO.builder()
						.ipAddress(generateIpAddress(network.getType()))
						.bandwidthUsage(random.nextDouble() * (network.getType().equalsIgnoreCase("WAN") ? 100 : 1000))
						.latency(generateLatency(network.getType(), random))
						.network(network) // Set the relationship with Network
						.build();

				// Add the simulated data to the list
				simulatedData.add(data);

				// Add NetworkDetails to the network (so that the report is updated)
				network.getSimulatedData().add(data);
			}

			// Save simulated data in NetworkDetails repository
			dataRepository.saveAll(simulatedData);

			// Save the network in the Network repository as well (if you want to update the network details)
			networkRepository.save(network); 

			return simulatedData;
		} catch (NetworkNotFoundException ex) {
			// Specifically manage the case where the network is not found
			throw new NetworkNotFoundException("Error while finding network with ID: " + networkId, ex);
		} catch (Exception ex) {
			// Manage other generic errors with a NetworkSimulatorException
			throw new NetworkSimulatorException("Error while simulating network with ID: " + networkId, ex);
		}
	}

	private String generateIpAddress(String type) {
		Random random = new Random();
		if (type.equalsIgnoreCase("LAN")) {
			// Simulates IP for local networks (192.168.x.x)
			return "192.168." + random.nextInt(256) + "." + random.nextInt(256);
		} else {
			// Simulates random WAN IPs
			return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "."
					+ random.nextInt(256);
		}
	}

	private double generateLatency(String type, Random random) {
		return type.equalsIgnoreCase("LAN") ? 1 + random.nextDouble() * 50 : 50 + random.nextDouble() * 250;
	}
}
