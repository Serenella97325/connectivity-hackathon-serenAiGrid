package com.project.networkDataManagement.models;

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
    
    // String for ISO 8601 formatting
    private String effectiveDateTime;
}
