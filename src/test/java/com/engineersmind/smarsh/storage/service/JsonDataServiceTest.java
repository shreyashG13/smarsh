package com.engineersmind.smarsh.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;

public class JsonDataServiceTest {

    private JsonDataService jsonDataService;

    @Mock
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private final String baseUrl = "https://example.com/api";
    private final String authToken = "yourAuthToken";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        jsonDataService = new JsonDataService(restTemplate, baseUrl, authToken);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testFetchJsonData() {
        // Arrange
        String date = "2023-10-05";
        boolean historicalData = true;

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.set("Authorization", "Bearer " + authToken);

        String expectedUrl = baseUrl + "?date=" + date + "&historicalData=" + historicalData + "&taskId=" + authToken;

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Your JSON data");

        mockServer.expect(once(), MockRestRequestMatchers.requestTo(expectedUrl))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer " + authToken))
                .andRespond(MockRestResponseCreators.withSuccess("Your JSON data", null));

        // Act
        String jsonData = jsonDataService.fetchJsonData(date, historicalData);

        // Assert
        assertThat(jsonData).isEqualTo(expectedResponse.getBody());

        // Verify that the RestTemplate was called with the expected request
        mockServer.verify();
    }
}
