package com.project.networkDataManagement.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.project.networkDataManagement.models.MonitoringDataDO;
import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.services.MonitoringService;
import com.project.networkDataManagement.utils.ApiResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class MonitoringControllerTest {

    @Mock
    private MonitoringService monitoringService;
    
    @InjectMocks
    private MonitoringController monitoringController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoringController).build();
    }

    @Test
    public void testGetNetworkMonitoring_Success() throws Exception {
        // Sample data
        String networkId = "testNetworkId";
        
        // Build the Network objectk
        NetworkDO mockNetwork = NetworkDO.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Build a list of NetworkDetails
        NetworkDetailsDO mockNetworkDetails = NetworkDetailsDO.builder()
                .id("testDetailsId")
                .ipAddress("192.168.0.1")
                .bandwidthUsage(500)
                .latency(20)
                .network(mockNetwork) // Associa il Network
                .build();

        List<NetworkDetailsDO> mockNetworkDetailsList = Arrays.asList(mockNetworkDetails);

        // Build the MonitoringData object
        MonitoringDataDO mockMonitoringData = MonitoringDataDO.builder()
                .networkDetails(mockNetwork)
                .networkMetrics(mockNetworkDetailsList)
                .timestamp(LocalDateTime.now())
                .build();

        // Building the ApiResponse of success
        ApiResponse<MonitoringDataDO> mockApiResponse = ApiResponse.success(mockMonitoringData);

        // Stub of service
        when(monitoringService.collectMonitoringData(networkId)).thenReturn(mockApiResponse);

        // We execute the GET request
        mockMvc.perform(get("/monitoring/network/{networkId}", networkId))
                .andExpect(status().isOk())  // Check that the status is 200
                .andExpect(jsonPath("$.success").value(true))  // Check the success
                .andExpect(jsonPath("$.data.networkDetails.name").value("Test Network"))  // Check the details of the network
                .andExpect(jsonPath("$.data.networkDetails.type").value("LAN"))
                .andExpect(jsonPath("$.data.networkDetails.nodeCount").value(5))
                .andExpect(jsonPath("$.data.networkMetrics[0].ipAddress").value("192.168.0.1"))
                .andExpect(jsonPath("$.data.networkMetrics[0].bandwidthUsage").value(500));
    }

    @Test
    public void testGetNetworkMonitoring_Failure() throws Exception {
        String networkId = "testNetworkId";

        // Stub of service
        when(monitoringService.collectMonitoringData(networkId)).thenReturn(ApiResponse.failure("Network not found"));

        // We execute the GET request to obtain network monitoring data
        mockMvc.perform(get("/monitoring/network/{networkId}", networkId))
                .andExpect(status().isInternalServerError())  // Check that the answer is 500
                .andExpect(jsonPath("$.errorMessages[0]").value("Network not found"));  // Check the error message
    }
}