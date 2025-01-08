package com.project.network.serenaigrid.medicalDataManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.network.serenaigrid.medicalDataManagement.models.ObservationDO;

@Repository
public interface ObservationRepository extends JpaRepository<ObservationDO, String>{

}
