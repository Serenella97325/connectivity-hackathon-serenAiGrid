package com.project.networkDataManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.networkDataManagement.models.NetworkDetailsDO;

@Repository
public interface NetworkDetailsRepository extends JpaRepository<NetworkDetailsDO, String> {

}
