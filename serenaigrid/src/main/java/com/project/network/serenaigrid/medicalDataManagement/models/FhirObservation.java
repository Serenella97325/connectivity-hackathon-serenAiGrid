package com.project.network.serenaigrid.medicalDataManagement.models;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fhir_observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FhirObservation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5433291820514010728L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String resourceId; // UUID del resource FHIR

    private String status;

    private String code;

    private String display;

    private String comment;

    private Date effectiveDate;

    @Column(columnDefinition = "TEXT")
    private String extensions; // Estensioni come JSON

}
