package com.project.network.serenaigrid.controllers;

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

import com.project.network.serenaigrid.networkManagement.controllers.MonitoringController;
import com.project.network.serenaigrid.networkManagement.models.MonitoringDataDO;
import com.project.network.serenaigrid.networkManagement.models.NetworkDO;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetailsDO;
import com.project.network.serenaigrid.networkManagement.services.MonitoringService;
import com.project.network.serenaigrid.utils.ApiResponse;

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
        // Dati di esempio
        String networkId = "testNetworkId";
        
        // Costruisco l'oggetto Network
        NetworkDO mockNetwork = NetworkDO.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Costruisco una lista di NetworkDetails
        NetworkDetailsDO mockNetworkDetails = NetworkDetailsDO.builder()
                .id("testDetailsId")
                .ipAddress("192.168.0.1")
                .bandwidthUsage(500)
                .latency(20)
                .network(mockNetwork) // Associa il Network
                .build();

        List<NetworkDetailsDO> mockNetworkDetailsList = Arrays.asList(mockNetworkDetails);

        // Costruisco l'oggetto MonitoringData
        MonitoringDataDO mockMonitoringData = MonitoringDataDO.builder()
                .networkDetails(mockNetwork)
                .networkMetrics(mockNetworkDetailsList)
                .timestamp(LocalDateTime.now())
                .build();

        // Costruisco l'ApiResponse di successo
        ApiResponse<MonitoringDataDO> mockApiResponse = ApiResponse.success(mockMonitoringData);

        // Stub del servizio
        when(monitoringService.collectMonitoringData(networkId)).thenReturn(mockApiResponse);

        // Eseguiamo la richiesta GET
        mockMvc.perform(get("/monitoring/network/{networkId}", networkId))
                .andExpect(status().isOk())  // Verifica che lo stato sia 200
                .andExpect(jsonPath("$.success").value(true))  // Verifica il successo
                .andExpect(jsonPath("$.data.networkDetails.name").value("Test Network"))  // Verifica i dettagli della rete
                .andExpect(jsonPath("$.data.networkDetails.type").value("LAN"))
                .andExpect(jsonPath("$.data.networkDetails.nodeCount").value(5))
                .andExpect(jsonPath("$.data.networkMetrics[0].ipAddress").value("192.168.0.1"))
                .andExpect(jsonPath("$.data.networkMetrics[0].bandwidthUsage").value(500));
    }

    @Test
    public void testGetNetworkMonitoring_Failure() throws Exception {
        String networkId = "testNetworkId";

        // Stub del servizio per lanciare un'eccezione generica
        when(monitoringService.collectMonitoringData(networkId)).thenReturn(ApiResponse.failure("Network not found"));

        // Eseguiamo la richiesta GET per ottenere i dati di monitoraggio della rete
        mockMvc.perform(get("/monitoring/network/{networkId}", networkId))
                .andExpect(status().isInternalServerError())  // Verifica che la risposta sia 500
                .andExpect(jsonPath("$.errorMessages[0]").value("Network not found"));  // Verifica il messaggio di errore
    }
}