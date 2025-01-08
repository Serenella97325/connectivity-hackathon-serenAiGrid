package com.project.network.serenaigrid.medicalDataManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.network.serenaigrid.medicalDataManagement.models.DeviceDO;

public interface DeviceRepository extends JpaRepository<DeviceDO, String>{

}
