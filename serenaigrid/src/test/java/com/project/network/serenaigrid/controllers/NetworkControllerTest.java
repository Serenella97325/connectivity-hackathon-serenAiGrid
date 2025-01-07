package com.project.network.serenaigrid.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.project.network.serenaigrid.networkManagement.controllers.NetworkController;
import com.project.network.serenaigrid.networkManagement.dtos.ApiResponse;
import com.project.network.serenaigrid.networkManagement.models.Network;
import com.project.network.serenaigrid.networkManagement.services.NetworkService;

@SpringBootTest
public class NetworkControllerTest {

    @Mock
    private NetworkService networkService;

    @InjectMocks
    private NetworkController networkController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(networkController).build();
    }
    
    @Test
    public void testRegisterNetwork_Success() throws Exception {
        // Prepariamo un oggetto mock di rete
        Network mockNetwork = Network.builder()
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Costruisci una risposta di successo (ApiResponse)
        ApiResponse<Network> mockApiResponse = ApiResponse.success(mockNetwork);

        // Configurazione dello stub del servizio
        when(networkService.registerNetwork(any(Network.class))).thenReturn(mockApiResponse);

        // Eseguiamo la richiesta POST per registrare una rete
        mockMvc.perform(post("/network/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Test Network",
                        "type": "LAN",
                        "nodeCount": 5,
                        "description": "Test description"
                    }
                """))
                .andExpect(status().isCreated())  // Verifica che lo status sia 201 Created
                .andExpect(jsonPath("$.success").value(true))  // Verifica che success sia true
                .andExpect(jsonPath("$.data.name").value("Test Network"))  // Verifica i dettagli della rete
                .andExpect(jsonPath("$.data.type").value("LAN"))
                .andExpect(jsonPath("$.data.nodeCount").value(5));  // Verifica che i valori siano corretti
    }
    
    @Test
    public void testRegisterNetwork_Failure() throws Exception {
        // Simula un errore nel servizio (esempio NetworkServiceException)
        when(networkService.registerNetwork(any(Network.class))).thenReturn(ApiResponse.failure("Error while registering the network"));

        // Eseguiamo la richiesta POST per registrare una rete
        mockMvc.perform(post("/network/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Test Network",
                        "type": "LAN",
                        "nodeCount": 5,
                        "description": "Test description"
                    }
                """))
                .andExpect(status().isInternalServerError())  // Verifica che lo status sia 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Verifica che success sia false
                .andExpect(jsonPath("$.errorMessages[0]").value("Error while registering the network"));  // Verifica il messaggio di errore
    }
    
    @Test
    public void testListNetworks_Success() throws Exception {
        // Prepariamo una lista di reti mock
        Network mockNetwork1 = Network.builder()
                .name("Network 1")
                .type("LAN")
                .nodeCount(10)
                .description("Description 1")
                .build();

        Network mockNetwork2 = Network.builder()
                .name("Network 2")
                .type("WAN")
                .nodeCount(20)
                .description("Description 2")
                .build();

        // Configuriamo lo stub del servizio per restituire una lista di reti
        ApiResponse<List<Network>> mockApiResponse = ApiResponse.success(Arrays.asList(mockNetwork1, mockNetwork2));
        when(networkService.getAllNetworks()).thenReturn(mockApiResponse);

        // Eseguiamo la richiesta GET per ottenere la lista delle reti
        mockMvc.perform(get("/network/list"))
                .andExpect(status().isOk())  // Verifica che lo status sia 200 OK
                .andExpect(jsonPath("$.success").value(true))  // Verifica il successo
                .andExpect(jsonPath("$.data.size()").value(2))  // Verifica che la dimensione della lista sia 2
                .andExpect(jsonPath("$.data.[0].name").value("Network 1"))  // Verifica il nome del primo network
                .andExpect(jsonPath("$.data.[1].name").value("Network 2"));  // Verifica il nome del secondo network
    }
    
    @Test
    public void testListNetworks_Failure() throws Exception {
        // Simula un errore nel servizio (esempio NetworkNotFoundException)
        when(networkService.getAllNetworks()).thenReturn(ApiResponse.failure("Networks not found"));

        // Eseguiamo la richiesta GET per ottenere la lista delle reti
        mockMvc.perform(get("/network/list"))
                .andExpect(status().isInternalServerError())  // Verifica che lo status sia 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Verifica che success sia false
                .andExpect(jsonPath("$.errorMessages[0]").value("Networks not found"));  // Verifica il messaggio di errore
    }
    
    @Test
    public void testGetNetworkById_Success() throws Exception {
        // Dati di esempio
        String networkId = "testNetworkId";
    	
    	// Prepariamo un oggetto mock di rete
        Network mockNetwork = Network.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Costruisci una risposta di successo (ApiResponse)
        ApiResponse<Network> mockApiResponse = ApiResponse.success(mockNetwork);

        // Configurazione dello stub del servizio
        when(networkService.getNetworkById(networkId)).thenReturn(mockApiResponse);

        // Eseguiamo la richiesta GET per ottenere una rete per ID
        mockMvc.perform(get("/network/network/{networkId}", "testNetworkId"))
                .andExpect(status().isOk())  // Verifica che lo status sia 200 OK
                .andExpect(jsonPath("$.success").value(true))  // Verifica che success sia true
                .andExpect(jsonPath("$.data.name").value("Test Network"))  // Verifica i dettagli della rete
                .andExpect(jsonPath("$.data.type").value("LAN"))
                .andExpect(jsonPath("$.data.nodeCount").value(5));  // Verifica che i valori siano corretti
    }
    
    @Test
    public void testGetNetworkById_Failure() throws Exception {
    	String networkId = "testNetworkId";
    	
        // Simula un errore nel servizio (esempio NetworkNotFoundException)
        when(networkService.getNetworkById(networkId)).thenReturn(ApiResponse.failure("Network not found"));

        // Eseguiamo la richiesta GET per ottenere una rete per ID
        mockMvc.perform(get("/network/network/{networkId}", "testNetworkId"))
                .andExpect(status().isInternalServerError())  // Verifica che lo status sia 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Verifica che success sia false
                .andExpect(jsonPath("$.errorMessages[0]").value("Network not found"));  // Verifica il messaggio di errore
    }  
}
