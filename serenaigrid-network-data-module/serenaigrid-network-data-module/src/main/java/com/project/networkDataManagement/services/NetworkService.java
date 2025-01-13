package com.project.networkDataManagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.services.exceptions.NetworkNotFoundException;
import com.project.networkDataManagement.services.exceptions.NetworkServiceException;
import com.project.networkDataManagement.utils.ApiResponse;

@Service
public class NetworkService {

	private NetworkRepository networkRepository;
	
	@Autowired
    public NetworkService(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

	// Method for registering a network
	public ApiResponse<NetworkDO> registerNetwork(NetworkDO network) {
		try {
			if (network == null) {
				throw new NetworkServiceException("Error while registering the network");
			}
			NetworkDO savedNetwork = networkRepository.save(network);
			return ApiResponse.success(savedNetwork); // Returns a successful response with the registered network
		} catch (NetworkServiceException ex) {
			return ApiResponse.failure(ex.getMessage());
		}
	}

	// Method to obtain all networks
	public ApiResponse<List<NetworkDO>> getAllNetworks() {
		try {
			List<NetworkDO> networks = networkRepository.findAll();
			if (networks == null || networks.isEmpty()) {
				throw new NetworkNotFoundException("Networks not found");
			}
			return ApiResponse.success(networks); // Returns a successful response with all networks
		} catch (NetworkNotFoundException ex) {
			return ApiResponse.failure(ex.getMessage()); // Returns an error if no networks are found
		}
	}

	// Method to get a network by ID
	public ApiResponse<NetworkDO> getNetworkById(String networkId) {
		try {
			NetworkDO network = networkRepository.findById(networkId)
					.orElseThrow(() -> new NetworkNotFoundException("Network not found"));
			return ApiResponse.success(network); // Returns a successful response with the network found
		} catch (NetworkNotFoundException ex) {
			return ApiResponse.failure(ex.getMessage()); // Returns an error if the network was not found
		}
	}

}
