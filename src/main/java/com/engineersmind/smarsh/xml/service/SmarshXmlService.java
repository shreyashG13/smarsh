package com.engineersmind.smarsh.xml.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;

import com.engineersmind.smarsh.xml.model.Root;

public interface SmarshXmlService {

	ResponseEntity<?> getApiRequest() throws MalformedURLException, IOException, ParseException;

	



}
