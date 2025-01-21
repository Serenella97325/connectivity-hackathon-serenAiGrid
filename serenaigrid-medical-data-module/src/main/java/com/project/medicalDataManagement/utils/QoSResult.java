package com.project.medicalDataManagement.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QoSResult {

	private double qos;
	private double bandwidth;
	private double latency;

}
