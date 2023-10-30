package com.engineersmind.smarsh.xml.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SmarshXmlServiceTest {

    private SmarshXmlService smarshXmlService;

    @BeforeEach
    public void setUp() {

        // Create an instance test implementation
        smarshXmlService = new SmarshXmlServiceTestImpl();
    }

    @Test
    public void testGetApiRequest() throws MalformedURLException, IOException, ParseException {
        // Call the getApiRequest method to obtain the result
        ResponseEntity<?> responseEntity = smarshXmlService.getApiRequest();

        // Verify that the response is not null
        assertNotNull(responseEntity);

        // Verify the expected XML content in the response
        String expectedXmlData = "<xml>...</xml>";
        assertEquals(expectedXmlData, responseEntity.getBody());
    }

    private static class SmarshXmlServiceTestImpl implements SmarshXmlService {
        @Override
        public ResponseEntity<?> getApiRequest() throws  IOException {

            String mockXmlData = "<xml>...</xml>";
            return ResponseEntity.ok(mockXmlData);
        }

		@Override
		public ResponseEntity<?> getApiRequestpastdata(String startDate, String endDate)
				throws MalformedURLException, IOException, ParseException {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
