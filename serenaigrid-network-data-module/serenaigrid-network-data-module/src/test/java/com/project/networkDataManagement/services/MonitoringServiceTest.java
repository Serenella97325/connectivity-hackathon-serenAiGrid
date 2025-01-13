package com.project.networkDataManagement.services;

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

import com.project.networkDataManagement.models.MonitoringDataDO;
import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.utils.ApiResponse;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MonitoringServiceTest {
    
    @Mock
    private NetworkRepository networkRepository;  

    @Mock
    private NetworkSimulatorService simulatorService;  

    @InjectMocks
    private MonitoringService monitoringService;  // The service to be tested

    @BeforeEach
    public void setUp() {
        // Settings before each test (if necessary)
    }

    @Test
    public void testCollectMonitoringData() {
        // We perform the 3 cases in a single test
        
        testCollectMonitoringData_Success();
        testCollectMonitoringData_SimulationFailure();
        testCollectMonitoringData_NetworkNotFound();
    }

    private void testCollectMonitoringData_Success() {
        String networkId = "testNetworkId";

        // Build the Network object
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
                .network(mockNetwork) // Join the Network
                .build();

        List<NetworkDetailsDO> mockNetworkDetailsList = Arrays.asList(mockNetworkDetails);

        // Stub of services
        when(networkRepository.findById(networkId)).thenReturn(Optional.of(mockNetwork));
        when(simulatorService.simulateNetwork(networkId)).thenReturn(mockNetworkDetailsList);

        // call to service
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Check that the answer is a success
        Assertions.assertTrue(result.isSuccess());

        // Check the data returned
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals("Test Network", result.getData().getNetworkDetails().getName());
        Assertions.assertEquals(5, result.getData().getNetworkDetails().getNodeCount());
        Assertions.assertEquals(1, result.getData().getNetworkMetrics().size());
        Assertions.assertEquals("192.168.0.1", result.getData().getNetworkMetrics().get(0).getIpAddress());
        Assertions.assertEquals(500, result.getData().getNetworkMetrics().get(0).getBandwidthUsage());
    }

    private void testCollectMonitoringData_SimulationFailure() {
        String networkId = "testNetworkId";

        // Build the Network object
        NetworkDO mockNetwork = NetworkDO.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Stub del servizio networkRepository per restituire il network mock
        when(networkRepository.findById(networkId)).thenReturn(Optional.of(mockNetwork));

        // Stub of the simulatorService service to return null or an empty list
        when(simulatorService.simulateNetwork(networkId)).thenReturn(Collections.emptyList());

        // call to service
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Check that the answer is not a success
        Assertions.assertFalse(result.isSuccess());

        // Check for errors in the errorMessages field
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Simulation failed"));
    }

    private void testCollectMonitoringData_NetworkNotFound() {
        String networkId = "testNetworkId";

        // Stub of the networkRepository service to not find the network
        when(networkRepository.findById(networkId)).thenReturn(Optional.empty());

        // call to service
        ApiResponse<MonitoringDataDO> result = monitoringService.collectMonitoringData(networkId);

        // Check that the answer is not a success
        Assertions.assertFalse(result.isSuccess());

        // Check for errors in the errorMessages field
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Network not found"));
    }
}

