package com.project.network.serenaigrid.medicalDataManagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationDO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Lob
    private String observationJson;

}
