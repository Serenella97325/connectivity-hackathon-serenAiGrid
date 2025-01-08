package com.project.network.serenaigrid.services;

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

import com.project.network.serenaigrid.networkManagement.models.NetworkDO;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.NetworkService;
import com.project.network.serenaigrid.utils.ApiResponse;

@SpringBootTest
public class NetworkServiceTest {

    @Mock
    private NetworkRepository networkRepository;  // Mock del repository

    @InjectMocks
    private NetworkService networkService;  // Il servizio che vogliamo testare

    @Test
    public void testRegisterNetwork_Success() {
        // Crea una rete da registrare
        NetworkDO network = NetworkDO.builder().name("Test Network").type("LAN").nodeCount(5).build();

        // Stub del repository per simulare il comportamento del salvataggio
        when(networkRepository.save(any(NetworkDO.class))).thenReturn(network);

        // Chiamata al servizio
        ApiResponse<NetworkDO> result = networkService.registerNetwork(network);

        // Verifica che la risposta sia di successo
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Test Network", result.getData().getName());
    }

    @Test
    public void testRegisterNetwork_Failure() {
        // Simula un errore nel servizio passando un oggetto nullo
        ApiResponse<NetworkDO> result = networkService.registerNetwork(null);

        // Verifica che la risposta non sia di successo
        Assertions.assertFalse(result.isSuccess());

        // Verifica che ci sia un errore nel campo errorMessages
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Error while registering the network"));
    }

    @Test
    public void testGetAllNetworks_Success() {
        List<NetworkDO> networks = List.of(
                NetworkDO.builder().name("Network 1").type("LAN").build(),
                NetworkDO.builder().name("Network 2").type("WAN").build()
        );

        // Stub del repository per restituire una lista di reti
        when(networkRepository.findAll()).thenReturn(networks);

        // Eseguiamo la chiamata al servizio
        ApiResponse<List<NetworkDO>> result = networkService.getAllNetworks();

        // Verifica che la risposta sia di successo
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(2, result.getData().size());
    }

    @Test
    public void testGetAllNetworks_Failure() {
        // Simula l'assenza di reti nel repository
        when(networkRepository.findAll()).thenReturn(Collections.emptyList());

        // Eseguiamo la chiamata al servizio
        ApiResponse<List<NetworkDO>> result = networkService.getAllNetworks();

        // Verifica che la risposta non sia di successo
        Assertions.assertFalse(result.isSuccess());

        // Verifica che ci sia un errore nel campo errorMessages
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Networks not found"));
    }

    @Test
    public void testGetNetworkById_Success() {
        // Prepara una rete e salvala nel repository
        NetworkDO network = NetworkDO.builder().name("Test Network").type("LAN").nodeCount(5).build();
        when(networkRepository.findById(network.getNetworkId())).thenReturn(Optional.of(network));

        // Chiamata al servizio per ottenere la rete tramite il suo ID
        ApiResponse<NetworkDO> result = networkService.getNetworkById(network.getNetworkId());

        // Verifica che la risposta sia di successo
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Test Network", result.getData().getName());
    }

    @Test
    public void testGetNetworkById_Failure() {
        // Simula l'assenza di una rete nel repository
        when(networkRepository.findById(anyString())).thenReturn(Optional.empty());

        // Chiamata al servizio per ottenere una rete con un ID inesistente
        ApiResponse<NetworkDO> result = networkService.getNetworkById("nonExistentId");

        // Verifica che la risposta non sia di successo
        Assertions.assertFalse(result.isSuccess());

        // Verifica che ci sia un errore nel campo errorMessages
        Assertions.assertNotNull(result.getErrorMessages());
        Assertions.assertTrue(result.getErrorMessages().contains("Network not found"));
    }
}


