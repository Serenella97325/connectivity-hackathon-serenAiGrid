package com.project.network.serenaigrid.services;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.network.serenaigrid.networkManagement.models.MonitoringDataDO;
import com.project.network.serenaigrid.networkManagement.models.NetworkDO;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetailsDO;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.MonitoringService;
import com.project.network.serenaigrid.networkManagement.services.NetworkSimulatorService;
import com.project.network.serenaigrid.utils.ApiResponse;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MonitoringServiceTest {
    
    @Mock
    private NetworkRepository networkRepository;  // Mock del repository

    @Mock
    private NetworkSimulatorService simulatorService;  // Mock del simulatore

    @InjectMocks
    private MonitoringService monitoringService;  // Il servizio da testare

    @BeforeEach
    public void setUp() {
        // Impostazioni prima di ogni test (se necessarie)
    }

    @Test
    public void testCollectMonitoringData() {
        // Eseguiamo i 3 casi in un unico test
        
        testCollectMonitoringData_Success();
        testCollectMonitoringData_SimulationFailure();
        testCollectMonitoringData_NetworkNotFound();
    }

    private void testCollectMonitoringData_Success() {
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

        // Stub dei servizi
        when(networkRepository.findById(networkId)).thenReturn(Optional.of(mockNetwork));
        when(simulatorService.simulateNetwork(networkId)).thenReturn(mockNetworkDetailsList);

        // Chiamata al servizio
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Verifica che la risposta sia un successo
        Assertions.assertTrue(result.isSuccess());

        // Verifica i dati restituiti
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals("Test Network", result.getData().getNetworkDetails().getName());
        Assertions.assertEquals(5, result.getData().getNetworkDetails().getNodeCount());
        Assertions.assertEquals(1, result.getData().getNetworkMetrics().size());
        Assertions.assertEquals("192.168.0.1", result.getData().getNetworkMetrics().get(0).getIpAddress());
        Assertions.assertEquals(500, result.getData().getNetworkMetrics().get(0).getBandwidthUsage());
    }

    private void testCollectMonitoringData_SimulationFailure() {
        String networkId = "testNetworkId";

        // Costruisco l'oggetto Network
        NetworkDO mockNetwork = NetworkDO.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Stub del servizio networkRepository per restituire il network mock
        when(networkRepository.findById(networkId)).thenReturn(Optional.of(mockNetwork));

        // Stub del servizio simulatorService per restituire null o una lista vuota
        when(simulatorService.simulateNetwork(networkId)).thenReturn(Collections.emptyList());

        // Chiamata al servizio
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Verifica che la risposta non sia un successo
        Assertions.assertFalse(result.isSuccess());

        // Verifica che ci sia un errore nel campo errorMessages
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Simulation failed"));
    }

    private void testCollectMonitoringData_NetworkNotFound() {
        String networkId = "testNetworkId";

        // Stub del servizio networkRepository per non trovare la rete
        when(networkRepository.findById(networkId)).thenReturn(Optional.empty());

        // Chiamata al servizio
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Verifica che la risposta non sia un successo
        Assertions.assertFalse(result.isSuccess());

        // Verifica che ci sia un errore nel campo errorMessages
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Network not found"));
    }
}

