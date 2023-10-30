package com.engineersmind.smarsh.xml.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;

import com.engineersmind.smarsh.xml.model.Root;

public interface SmarshXmlService {
	//To fetch regular Data based on current Date 
	ResponseEntity<?> getApiRequest() throws MalformedURLException, IOException, ParseException;

	
	// API to get PAST Data in Between Date range
	ResponseEntity<?> getApiRequestpastdata(String startDate, String endDate) throws MalformedURLException, IOException, ParseException;



}
