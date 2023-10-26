package com.engineersmind.smarsh.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.engineersmind.smarsh.storage.service.JsonDataService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class JsonDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JsonDataService jsonDataService;

    private JsonDataController jsonDataController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jsonDataController = new JsonDataController(jsonDataService);
        mockMvc = MockMvcBuilders.standaloneSetup(jsonDataController).build();
    }

    @Test
    public void testGetJsonData() throws Exception {
        // The date for testing. In your real tests, this might be dynamically calculated
        String testDate = "2023-10-25";
        String mockJsonResponse = "mocked JSON response";

        // Mock the service to return a successful response
        when(jsonDataService.fetchJsonData(testDate)).thenReturn(mockJsonResponse);

        mockMvc.perform(get("/json-data/{date}", testDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mockJsonResponse));

        verify(jsonDataService, times(1)).fetchJsonData(testDate);
    }
}
