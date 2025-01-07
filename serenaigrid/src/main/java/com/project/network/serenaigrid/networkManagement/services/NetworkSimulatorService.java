package com.project.network.serenaigrid.networkManagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetails;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkDetailsRepository;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkNotFoundException;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkSimulatorException;

@Service
public class NetworkSimulatorService {

	@Autowired
	private NetworkRepository networkRepository;

	@Autowired
	private NetworkDetailsRepository dataRepository;

	public List<NetworkDetails> simulateNetwork(String networkId) {
		try {
			// Recupera l'oggetto Network o lancia un'eccezione se non trovato
			Network network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found with ID: " + networkId));

			List<NetworkDetails> simulatedData = new ArrayList<>();
			Random random = new Random();

			for (int i = 0; i < network.getNodeCount(); i++) {
				NetworkDetails data = NetworkDetails.builder()
						.ipAddress(generateIpAddress(network.getType()))
						.bandwidthUsage(random.nextDouble() * (network.getType().equalsIgnoreCase("WAN") ? 100 : 1000))
						.latency(generateLatency(network.getType(), random))
						.network(network) // Imposta la relazione con Network
						.build();

				// Aggiungi il dato simulato alla lista
				simulatedData.add(data);

				// Aggiungi NetworkDetails alla rete (così che la relazione venga aggiornata)
				network.getSimulatedData().add(data);
			}

			// Salva i dati simulati nel repository NetworkDetails
			dataRepository.saveAll(simulatedData);

			// Salva anche la rete nel repository Network (se vuoi aggiornare i dettagli
			// della rete)
			networkRepository.save(network); // Questo è necessario per mantenere aggiornata la lista di NetworkDetails
												// nella rete

			return simulatedData;
		} catch (NetworkNotFoundException ex) {
			// Gestisci specificamente il caso in cui la rete non è trovata
			throw new NetworkNotFoundException("Error while finding network with ID: " + networkId, ex);
		} catch (Exception ex) {
			// Gestisci altri errori generici con una NetworkSimulatorException
			throw new NetworkSimulatorException("Error while simulating network with ID: " + networkId, ex);
		}
	}

	private String generateIpAddress(String type) {
		Random random = new Random();
		if (type.equalsIgnoreCase("LAN")) {
			// Simula IP per reti locali (192.168.x.x)
			return "192.168." + random.nextInt(256) + "." + random.nextInt(256);
		} else {
			// Simula IP WAN casuali
			return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "."
					+ random.nextInt(256);
		}
	}

	private double generateLatency(String type, Random random) {
		return type.equalsIgnoreCase("LAN") ? 1 + random.nextDouble() * 50 : 50 + random.nextDouble() * 250;
	}
}
