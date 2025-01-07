package com.project.network.serenaigrid.networkManagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.network.serenaigrid.networkManagement.models.Network;

@Repository
public interface NetworkRepository extends JpaRepository<Network, String> {
	
	Optional<Network> findById(String networkId);

	// Network findByNetworkId(String networkId);
}
