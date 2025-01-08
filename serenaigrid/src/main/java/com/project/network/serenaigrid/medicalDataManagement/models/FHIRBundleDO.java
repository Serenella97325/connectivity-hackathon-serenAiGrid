package com.project.network.serenaigrid.medicalDataManagement.models;

import java.io.Serializable;
import java.util.List;

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
public class FHIRBundleDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8832044927311713665L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Lob
    private List<String> observationIds;

    @Lob
    private List<String> deviceIds;
    
    @Lob
    private String bundleJson;

}
