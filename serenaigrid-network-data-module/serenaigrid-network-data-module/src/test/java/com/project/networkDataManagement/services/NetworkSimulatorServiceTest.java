package com.project.networkDataManagement.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.models.NetworkDetailsDO;
import com.project.networkDataManagement.repositories.NetworkDetailsRepository;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.services.exceptions.NetworkNotFoundException;
import com.project.networkDataManagement.services.exceptions.NetworkSimulatorException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class NetworkSimulatorServiceTest {

    @Mock
    private NetworkRepository networkRepository; 

    @Mock
    private NetworkDetailsRepository dataRepository; 
    
    @InjectMocks
    private NetworkSimulatorService networkSimulatorService; // Service to be tested
    
    @BeforeEach
    public void setUp() {
        // Settings before each test (if necessary)
    }

    @Test
    public void testSimulateNetworkSuccess() {
        // Create a network to record
        NetworkDO network = NetworkDO.builder().name("Test Network").type("LAN").nodeCount(5).build();
        
        // Configure the behavior of the repository mock
        when(networkRepository.findById(network.getNetworkId())).thenReturn(Optional.of(network));

        // Run the simulation method
        List<NetworkDetailsDO> simulatedData = networkSimulatorService.simulateNetwork(network.getNetworkId());

        // Check that the simulation has been successful
        assertNotNull(simulatedData);
        assertEquals(5, simulatedData.size()); // Verify that the number of simulated data matches the network nodeCount
        verify(dataRepository, times(1)).saveAll(simulatedData); // Verify that the simulated data has been saved
    }

    @Test
    public void testSimulateNetworkNotFound() {
        String nonExistingNetworkId = "nonExistingId";  // un ID che non esiste nel database

        // Configure the mock behavior to return Optional.empty when no network is found
        when(networkRepository.findById(nonExistingNetworkId)).thenReturn(Optional.empty());

        // Verify that a NetworkNotFoundException exception is launched
        assertThrows(NetworkNotFoundException.class, () -> {
            networkSimulatorService.simulateNetwork(nonExistingNetworkId);
        });
    }

    @Test
    public void testSimulateNetworkGenericException() {
        // Configure the behavior of the mock to generate an exception
        when(networkRepository.findById("network-id")).thenThrow(new RuntimeException("Database error"));

        // Run the method and verify that the NetworkSimulatorException exception is launched
        Exception exception = assertThrows(NetworkSimulatorException.class, () -> {
            networkSimulatorService.simulateNetwork("network-id");
        });

        assertTrue(exception.getMessage().contains("Error while simulating network with ID: network-id"));
    }
}