package com.engineersmind.smarsh.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JsonDataServiceTest {

    private JsonDataService jsonDataService;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        jsonDataService = new JsonDataService(restTemplate, "http://example.com/api", "yourAuthToken");
    }

    @Test
    public void testFetchJsonData() {
        // Mock the HTTP response
        String jsonResponse = "{\"key\":\"value\"}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);

        when(restTemplate.exchange(
                eq("http://example.com/api?date=2023-10-16&historicalData=true&taskId=yourAuthToken"),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenReturn(responseEntity);

        // Call the service method
        String result = jsonDataService.fetchJsonData("2023-10-16", true);

        // Assert the result
        assertEquals(jsonResponse, result);
    }
}
