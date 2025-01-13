package com.project.networkDataManagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.networkDataManagement.models.NetworkDO;

@Repository
public interface NetworkRepository extends JpaRepository<NetworkDO, String> {
	
	Optional<NetworkDO> findById(String networkId);
}
