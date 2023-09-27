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

 public String fetchJsonData(String date, boolean historicalData) {
     try {
         HttpHeaders headers = new HttpHeaders();
         headers.set("Authorization", "Bearer " + authToken);

         // Using authToken as taskId
         String fullUrl = baseUrl + "?date=" + date + "&historicalData=" + historicalData + "&taskId=" + authToken;

         HttpEntity<String> entity = new HttpEntity<>(headers);

         ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);

         return response.getBody();
     } catch (Exception e) {
         e.printStackTrace();
         return null;
     }
 }
}
