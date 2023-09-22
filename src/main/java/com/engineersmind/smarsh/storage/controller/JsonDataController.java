package com.engineersmind.smarsh.storage.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineersmind.smarsh.storage.service.JsonDataService;



@RestController
public class JsonDataController {

    private final JsonDataService jsonDataService;

    @Autowired
    public JsonDataController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @GetMapping("/json-data")
    public String getJsonData() {
        return jsonDataService.fetchJsonData();
    }
}
