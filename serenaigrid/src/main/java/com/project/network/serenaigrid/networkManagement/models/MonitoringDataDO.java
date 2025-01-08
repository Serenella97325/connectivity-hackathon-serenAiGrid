package com.project.network.serenaigrid.networkManagement.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonitoringDataDO {
	
    private NetworkDO networkDetails;
    
    private List<NetworkDetailsDO> networkMetrics;
    
    private LocalDateTime timestamp;
}
