package com.project.network.serenaigrid.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.models.NetworkDetails;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkDetailsRepository;
import com.project.network.serenaigrid.networkManagement.repositories.NetworkRepository;
import com.project.network.serenaigrid.networkManagement.services.NetworkSimulatorService;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkNotFoundException;
import com.project.network.serenaigrid.networkManagement.services.exceptions.NetworkSimulatorException;

@SpringBootTest
public class NetworkSimulatorServiceTest {

    @Mock
    private NetworkRepository networkRepository; // Mock del repository della rete

    @Mock
    private NetworkDetailsRepository dataRepository; // Mock del repository per i dati simulati
    
    @InjectMocks
    private NetworkSimulatorService networkSimulatorService; // Servizio da testare

//    private Network mockNetwork;
//
//    @BeforeEach
//    public void setUp() {
//        // Inizializzazione del mock per Network
//        mockNetwork = new Network();
//        mockNetwork.setNetworkId("network-id");
//        mockNetwork.setName("Test Network");
//        mockNetwork.setType("LAN");
//        mockNetwork.setNodeCount(5);
//        mockNetwork.setDescription("Test description");
//    }

    @Test
    public void testSimulateNetworkSuccess() {
        // Crea una rete da registrare
        Network network = Network.builder().name("Test Network").type("LAN").nodeCount(5).build();
        
        // Configura il comportamento del mock del repository
        when(networkRepository.findById(network.getNetworkId())).thenReturn(Optional.of(network));

        // Esegui il metodo di simulazione
        List<NetworkDetails> simulatedData = networkSimulatorService.simulateNetwork(network.getNetworkId());

        // Verifica che la simulazione sia andata a buon fine
        assertNotNull(simulatedData);
        assertEquals(5, simulatedData.size()); // Verifica che il numero di dati simulati corrisponda al nodeCount della rete
        verify(dataRepository, times(1)).saveAll(simulatedData); // Verifica che i dati simulati siano stati salvati
    }

    @Test
    public void testSimulateNetworkNotFound() {
        String nonExistingNetworkId = "nonExistingId";  // un ID che non esiste nel database

        // Configura il comportamento del mock per restituire Optional.empty quando non viene trovata la rete
        when(networkRepository.findById(nonExistingNetworkId)).thenReturn(Optional.empty());

        // Verifica che venga lanciata un'eccezione di tipo NetworkNotFoundException
        assertThrows(NetworkNotFoundException.class, () -> {
            networkSimulatorService.simulateNetwork(nonExistingNetworkId);
        });
    }

    @Test
    public void testSimulateNetworkGenericException() {
        // Configura il comportamento del mock per generare un'eccezione
        when(networkRepository.findById("network-id")).thenThrow(new RuntimeException("Database error"));

        // Esegui il metodo e verifica che venga lanciata l'eccezione NetworkSimulatorException
        Exception exception = assertThrows(NetworkSimulatorException.class, () -> {
            networkSimulatorService.simulateNetwork("network-id");
        });

        assertTrue(exception.getMessage().contains("Error while simulating network with ID: network-id"));
    }
}