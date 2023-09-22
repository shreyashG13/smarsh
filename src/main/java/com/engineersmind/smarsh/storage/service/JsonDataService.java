package com.engineersmind.smarsh.storage.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JsonDataService {

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final String authToken; // Your authorization token

    @Autowired
    public JsonDataService(
            RestTemplate restTemplate,
            @Value("${api.url}") String apiUrl,
            @Value("${api.auth.token}") String authToken) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.authToken = authToken;
    }

    public String fetchJsonData() {
        try {
            // Create HttpHeaders and set the authorization token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            // Create an HttpEntity with the headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the GET request with the entity
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            // Process the response
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
