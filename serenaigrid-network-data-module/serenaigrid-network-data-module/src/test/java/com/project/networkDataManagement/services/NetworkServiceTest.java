package com.project.networkDataManagement.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.repositories.NetworkRepository;
import com.project.networkDataManagement.utils.ApiResponse;

@SpringBootTest
public class NetworkServiceTest {

    @Mock
    private NetworkRepository networkRepository; 

    @InjectMocks
    private NetworkService networkService;  // The service we want to test

    @Test
    public void testRegisterNetwork_Success() {
        // Create a network to record
        NetworkDO network = NetworkDO.builder().name("Test Network").type("LAN").nodeCount(5).build();

        // Repository stub to simulate the behavior of the save
        when(networkRepository.save(any(NetworkDO.class))).thenReturn(network);

        // call to service
        ApiResponse<NetworkDO> result = networkService.registerNetwork(network);

        // Check that the answer is successful
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Test Network", result.getData().getName());
    }

    @Test
    public void testRegisterNetwork_Failure() {
        // Simulates an error in the service by passing a null object
        ApiResponse<NetworkDO> result = networkService.registerNetwork(null);

        // Check that the answer is not successful
        Assertions.assertFalse(result.isSuccess());

        // Check for errors in the errorMessages field
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Error while registering the network"));
    }

    @Test
    public void testGetAllNetworks_Success() {
        List<NetworkDO> networks = List.of(
                NetworkDO.builder().name("Network 1").type("LAN").build(),
                NetworkDO.builder().name("Network 2").type("WAN").build()
        );

        // Stub of the repository to return a list of networks
        when(networkRepository.findAll()).thenReturn(networks);

        // We perform the call to service
        ApiResponse<List<NetworkDO>> result = networkService.getAllNetworks();

        // Check that the answer is successful
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(2, result.getData().size());
    }

    @Test
    public void testGetAllNetworks_Failure() {
        //  Simulates no networks in the repository
        when(networkRepository.findAll()).thenReturn(Collections.emptyList());

        // We perform the call to service
        ApiResponse<List<NetworkDO>> result = networkService.getAllNetworks();

        // Check that the answer is not successful
        Assertions.assertFalse(result.isSuccess());

        // Check for errors in the errorMessages field
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Networks not found"));
    }

    @Test
    public void testGetNetworkById_Success() {
        // Prepare a network and save it in the repository
        NetworkDO network = NetworkDO.builder().name("Test Network").type("LAN").nodeCount(5).build();
        when(networkRepository.findById(network.getNetworkId())).thenReturn(Optional.of(network));

        // Call the service to get the network through its ID
        ApiResponse<NetworkDO> result = networkService.getNetworkById(network.getNetworkId());

        // Check that the answer is successful
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Test Network", result.getData().getName());
    }

    @Test
    public void testGetNetworkById_Failure() {
        // Simulates the absence of a network in the repository
        when(networkRepository.findById(anyString())).thenReturn(Optional.empty());

        // Call the service to get a network with an ID that does not exist
        ApiResponse<NetworkDO> result = networkService.getNetworkById("nonExistentId");

        // Check that the answer is not successful
        Assertions.assertFalse(result.isSuccess());

        // Check for errors in the errorMessages field
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Network not found"));
    }
}


