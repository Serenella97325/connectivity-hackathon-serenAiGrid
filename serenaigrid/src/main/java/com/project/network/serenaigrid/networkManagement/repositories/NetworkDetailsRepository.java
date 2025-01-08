package com.project.network.serenaigrid.networkManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.network.serenaigrid.networkManagement.models.NetworkDetailsDO;

@Repository
public interface NetworkDetailsRepository extends JpaRepository<NetworkDetailsDO, String> {

}
