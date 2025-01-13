package com.project.networkDataManagement.controllers;

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

import com.project.networkDataManagement.models.NetworkDO;
import com.project.networkDataManagement.services.NetworkService;
import com.project.networkDataManagement.utils.ApiResponse;

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
        // Prepare a mock network object
        NetworkDO mockNetwork = NetworkDO.builder()
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Build a successful response (ApiResponse)
        ApiResponse<NetworkDO> mockApiResponse = ApiResponse.success(mockNetwork);

        // Service stub configuration
        when(networkService.registerNetwork(any(NetworkDO.class))).thenReturn(mockApiResponse);

        // We execute the POST request to register a network
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
                .andExpect(status().isCreated())  // Verify that the status is 201 Created
                .andExpect(jsonPath("$.success").value(true))  // Verify that success is true
                .andExpect(jsonPath("$.data.name").value("Test Network"))  // Check the details of the network
                .andExpect(jsonPath("$.data.type").value("LAN"))
                .andExpect(jsonPath("$.data.nodeCount").value(5));  // Check that the values are correct
    }
    
    @Test
    public void testRegisterNetwork_Failure() throws Exception {
        // Simulates an error in the service (NetworkServiceException example)
        when(networkService.registerNetwork(any(NetworkDO.class))).thenReturn(ApiResponse.failure("Error while registering the network"));

        // We execute the POST request to register a network
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
                .andExpect(status().isInternalServerError())  // Check that the status is 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Check that success is false
                .andExpect(jsonPath("$.errorMessages[0]").value("Error while registering the network"));  // Check the error message
    }
    
    @Test
    public void testListNetworks_Success() throws Exception {
        // We prepare a list of mock networks
        NetworkDO mockNetwork1 = NetworkDO.builder()
                .name("Network 1")
                .type("LAN")
                .nodeCount(10)
                .description("Description 1")
                .build();

        NetworkDO mockNetwork2 = NetworkDO.builder()
                .name("Network 2")
                .type("WAN")
                .nodeCount(20)
                .description("Description 2")
                .build();

        // We configure the service stub to return a list of networks
        ApiResponse<List<NetworkDO>> mockApiResponse = ApiResponse.success(Arrays.asList(mockNetwork1, mockNetwork2));
        when(networkService.getAllNetworks()).thenReturn(mockApiResponse);

        // We execute the GET request to get the list of networks
        mockMvc.perform(get("/network/list"))
                .andExpect(status().isOk())  // Check that the status is 200 OK
                .andExpect(jsonPath("$.success").value(true))  // Check the success
                .andExpect(jsonPath("$.data.size()").value(2))  // Verify that the list size is 2
                .andExpect(jsonPath("$.data.[0].name").value("Network 1"))  // Check the name of the first network
                .andExpect(jsonPath("$.data.[1].name").value("Network 2"));  // Check the name of the second network
    }
    
    @Test
    public void testListNetworks_Failure() throws Exception {
        // Simulates an error in the service (example NetworkNotFoundException)
        when(networkService.getAllNetworks()).thenReturn(ApiResponse.failure("Networks not found"));

        // We execute the GET request to get the list of networks
        mockMvc.perform(get("/network/list"))
                .andExpect(status().isInternalServerError())  // Check that the status is 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Check that success is false
                .andExpect(jsonPath("$.errorMessages[0]").value("Networks not found"));  // Check the error message
    }
    
    @Test
    public void testGetNetworkById_Success() throws Exception {
        // Sample data
        String networkId = "testNetworkId";
    	
    	// We prepare a mock network object
        NetworkDO mockNetwork = NetworkDO.builder()
                .networkId(networkId)
                .name("Test Network")
                .type("LAN")
                .nodeCount(5)
                .description("Test description")
                .build();

        // Build a successful response (ApiResponse)
        ApiResponse<NetworkDO> mockApiResponse = ApiResponse.success(mockNetwork);

        // Service stub configuration
        when(networkService.getNetworkById(networkId)).thenReturn(mockApiResponse);

        // We execute the GET request to get a network by ID
        mockMvc.perform(get("/network/network/{networkId}", "testNetworkId"))
                .andExpect(status().isOk())  // Check that the status is 200 OK
                .andExpect(jsonPath("$.success").value(true))  // Verify that success is true
                .andExpect(jsonPath("$.data.name").value("Test Network"))  // Check the details of the network
                .andExpect(jsonPath("$.data.type").value("LAN"))
                .andExpect(jsonPath("$.data.nodeCount").value(5));  // Check that the values are correct
    }
    
    @Test
    public void testGetNetworkById_Failure() throws Exception {
    	String networkId = "testNetworkId";
    	
        // Simulates an error in the service (example NetworkNotFoundException)
        when(networkService.getNetworkById(networkId)).thenReturn(ApiResponse.failure("Network not found"));

        // Eseguiamo la richiesta GET per ottenere una rete per ID
        mockMvc.perform(get("/network/network/{networkId}", "testNetworkId"))
                .andExpect(status().isInternalServerError())  // Check that the status is 500 Internal Server Error
                .andExpect(jsonPath("$.success").value(false))  // Check that success is false
                .andExpect(jsonPath("$.errorMessages[0]").value("Network not found"));  // Check the error message
    }  
}
