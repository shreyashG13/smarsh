package com.engineersmind.smarsh.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.engineersmind.smarsh.storage.service.JsonDataService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class JsonDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JsonDataService jsonDataService;

    private JsonDataController jsonDataController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize annotated mocks

        // Create the controller instance with the mocked service
        jsonDataController = new JsonDataController(jsonDataService);

        // Create the MockMvc instance
        mockMvc = MockMvcBuilders.standaloneSetup(jsonDataController).build();
    }

    @Test
    public void testGetJsonData() throws Exception {
        // Arrange
        String date = "2023-10-05";
        boolean historicalData = true;
        String jsonData = "Your JSON Data"; // Replace with your expected JSON data

        // Mock the service method
        when(jsonDataService.fetchJsonData(date, historicalData)).thenReturn(jsonData);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/json-data/{date}/{historicalData}", date, historicalData)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(jsonData));

        // Verify that the service method was called with the correct arguments
        verify(jsonDataService, times(1)).fetchJsonData(date, historicalData);
    }
}
