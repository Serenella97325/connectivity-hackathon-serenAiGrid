package com.project.network.serenaigrid.controllers;

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

import com.project.network.serenaigrid.networkManagement.controllers.NetworkSimulatorController;
import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetails;
import com.project.network.serenaigrid.networkManagement.services.NetworkSimulatorService;

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

    	// Creazione dell'oggetto Network
    	Network network = new Network();
    	network.setNetworkId("1");
    	network.setName("Test Network");
    	network.setType("LAN");
    	network.setNodeCount(10);
    	network.setDescription("Description for Test Network");

    	// Creazione degli oggetti NetworkData associati alla rete
    	NetworkDetails networkData1 = NetworkDetails.builder()
    	        .ipAddress("192.168.1.1")
    	        .bandwidthUsage(100.0)
    	        .latency(10.0)
    	        .network(network) // Associa l'oggetto Network
    	        .build();

    	NetworkDetails networkData2 = NetworkDetails.builder()
    	        .ipAddress("192.168.1.2")
    	        .bandwidthUsage(150.0)
    	        .latency(20.0)
    	        .network(network) // Associa l'oggetto Network
    	        .build();

        // Configura il comportamento dello stub del servizio
        when(simulatorService.simulateNetwork("1")).thenReturn(List.of(networkData1, networkData2));

        // Esegui la richiesta GET per simulare i dati della rete
        mockMvc.perform(get("/network-simulator/simulate/1"))
                .andExpect(status().isOk()) // Verifica che lo status sia 200 OK
                .andExpect(jsonPath("$.size()").value(2)) // Verifica che la risposta contenga 2 dati di rete
                .andExpect(jsonPath("$[0].ipAddress").value("192.168.1.1"))
                .andExpect(jsonPath("$[0].bandwidthUsage").value(100.0))
                .andExpect(jsonPath("$[1].ipAddress").value("192.168.1.2"))
                .andExpect(jsonPath("$[1].bandwidthUsage").value(150.0));
    }
}
