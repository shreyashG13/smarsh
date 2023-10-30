package com.engineersmind.smarsh.xml.controller;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.engineersmind.smarsh.xml.service.SmarshXmlService;




@RestController
public class CreatingXmlController {
	
	@Autowired
	SmarshXmlService smarshXmlService;
	  
	@GetMapping("/generate-xmls")
	    public ResponseEntity<?> getJsonData() throws IOException, ParseException {
	        return smarshXmlService.getApiRequest();
	    }
	// Additional endpoint for fetching data for a range of dates
			@GetMapping("/generate-xmls-range")
			public ResponseEntity<?> getJsonDataForRange(
					@RequestParam("startDate") String startDate, 
					@RequestParam("endDate") String endDate) throws IOException, ParseException {
				return smarshXmlService.getApiRequestpastdata(startDate, endDate);
			}
		}
		
