package com.project.network.serenaigrid.networkManagement.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkDetailsDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4912217320938733620L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id; // ID primario per NetworkData

	private String ipAddress;

	private double bandwidthUsage;

	private double latency;
	
    @ManyToOne
    @JoinColumn(name = "network_id", nullable = false) // Foreign key
    @JsonIgnore
    private NetworkDO network; // Relazione con la rete
}
