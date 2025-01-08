package com.project.network.serenaigrid.networkManagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.network.serenaigrid.networkManagement.models.NetworkDO;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkNotFoundException;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkServiceException;
import com.project.network.serenaigrid.utils.ApiResponse;

@Service
public class NetworkService {

	@Autowired
	private NetworkRepository networkRepository;

	// Metodo per registrare una rete
	public ApiResponse<NetworkDO> registerNetwork(NetworkDO network) {
		try {
			if (network == null) {
				throw new NetworkServiceException("Error while registering the network");
			}
			NetworkDO savedNetwork = networkRepository.save(network);
			return ApiResponse.success(savedNetwork); // Restituisce una risposta di successo con la rete registrata
		} catch (NetworkServiceException ex) {
			return ApiResponse.failure(ex.getMessage());
		}
	}

	// Metodo per ottenere tutte le reti
	public ApiResponse<List<NetworkDO>> getAllNetworks() {
		try {
			List<NetworkDO> networks = networkRepository.findAll();
			if (networks == null || networks.isEmpty()) {
				throw new NetworkNotFoundException("Networks not found");
			}
			return ApiResponse.success(networks); // Restituisce una risposta di successo con tutte le reti
		} catch (NetworkNotFoundException ex) {
			return ApiResponse.failure(ex.getMessage()); // Restituisce un errore se non si trovano le reti
		}
	}

	// Metodo per ottenere una rete per ID
	public ApiResponse<NetworkDO> getNetworkById(String networkId) {
		try {
			NetworkDO network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found"));
			return ApiResponse.success(network); // Restituisce una risposta di successo con la rete trovata
		} catch (NetworkNotFoundException ex) {
			return ApiResponse.failure(ex.getMessage()); // Restituisce un errore se la rete non è stata trovata
		}
	}

}
