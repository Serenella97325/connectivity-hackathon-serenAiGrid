package com.project.networkDataManagement.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.services.NetworkSimulatorService;

@SpringBootTest
public class NetworkSimulatorControllerTest {

    @Mock
    private NetworkSimulatorService simulatorService;

    @InjectMocks
    private NetworkSimulatorController simulatorController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(simulatorController).build();
    }

    @Test
    public void testSimulateNetwork() throws Exception {

    	// Creation of the Network object
    	NetworkDO network = new NetworkDO();
    	network.setNetworkId("1");
    	network.setName("Test Network");
    	network.setType("LAN");
    	network.setNodeCount(10);
    	network.setDescription("Description for Test Network");

    	// Creation of NetworkData objects associated with the network
    	NetworkDetailsDO networkData1 = NetworkDetailsDO.builder()
    	        .ipAddress("192.168.1.1")
    	        .bandwidthUsage(100.0)
    	        .latency(10.0)
    	        .network(network) // Associate the Network object
    	        .build();

    	NetworkDetailsDO networkData2 = NetworkDetailsDO.builder()
    	        .ipAddress("192.168.1.2")
    	        .bandwidthUsage(150.0)
    	        .latency(20.0)
    	        .network(network) // Associate the Network object
    	        .build();

        // Configure the service stub behavior
        when(simulatorService.simulateNetwork("1")).thenReturn(List.of(networkData1, networkData2));

        // Run the GET request to simulate network data
        mockMvc.perform(get("/network-simulator/simulate/1"))
                .andExpect(status().isOk()) // Check that the status is 200 OK
                .andExpect(jsonPath("$.size()").value(2)) // Verify that the answer contains 2 network data
                .andExpect(jsonPath("$[0].ipAddress").value("192.168.1.1"))
                .andExpect(jsonPath("$[0].bandwidthUsage").value(100.0))
                .andExpect(jsonPath("$[1].ipAddress").value("192.168.1.2"))
                .andExpect(jsonPath("$[1].bandwidthUsage").value(150.0));
    }
}
