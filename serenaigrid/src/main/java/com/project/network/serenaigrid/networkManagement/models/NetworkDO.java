package com.project.network.serenaigrid.networkManagement.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9046075744165492783L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String networkId;

	private String name;

	private String type; // LAN o WAN

	private int nodeCount;

	private String description;

	@OneToMany(mappedBy = "network", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default // Garantisce che l'inizializzazione sia rispettata anche con il builder
	@JsonIgnore
	private List<NetworkDetailsDO> simulatedData = new ArrayList<>(); // Inizializzazione

}
