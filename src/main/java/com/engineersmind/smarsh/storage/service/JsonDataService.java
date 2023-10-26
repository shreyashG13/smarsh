package com.engineersmind.smarsh.storage.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


@Service
public class JsonDataService {

 private final String baseUrl; 
 private final RestTemplate restTemplate;
 private final String authToken; // It will act as both your authorization token and taskId

 @Autowired
 public JsonDataService(
         RestTemplate restTemplate,
         @Value("${api.base.url}") String baseUrl, 
         @Value("${api.auth.token}") String authToken) {
     this.restTemplate = restTemplate;
     this.baseUrl = baseUrl;
     this.authToken = authToken;
     
 }
 @Value("${api.historical.data}")
 private boolean historicalDataFromProp;
 public String fetchJsonData(String date) {
     try {
         HttpHeaders headers = new HttpHeaders();
        // headers.set("Authorization", "Bearer " + authToken);

         String fullUrl = baseUrl 
                        + "?date=" + date 
                        + "&historicalData=" + historicalDataFromProp 
                        + "&taskId=" + authToken;

         HttpEntity<String> entity = new HttpEntity<>(headers);

         ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);

         // Prettify the JSON
         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
         Object json = objectMapper.readValue(response.getBody(), Object.class);
         String prettyJson = objectMapper.writeValueAsString(json);

         return prettyJson;
     } catch (Exception e) {
         e.printStackTrace();
         return null;
     }
 }
}