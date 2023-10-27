package com.engineersmind.smarsh.xml.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.engineersmind.smarsh.xml.model.ChatRoom;
import com.engineersmind.smarsh.xml.model.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SmarshXmlServiceImplTest {

    private SmarshXmlServiceImpl smarshXmlService;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        smarshXmlService = new SmarshXmlServiceImpl();
        restTemplate = Mockito.mock(RestTemplate.class);

    }


    @Test
    public void testCreatingXMLwithJson() throws IOException, ParseException {
        // Create a sample Root object for the test
        Root sampleRoot = new Root();
        // Populate the sampleRoot object with data
        sampleRoot.setChatRooms((ArrayList<ChatRoom>) createSampleChatRooms());
        // Call the method to be tested
        ResponseEntity<?> result = smarshXmlService.creatingXMLwithJson(sampleRoot);
        // Assertions
        assertEquals(HttpStatus.OK, result.getStatusCode());

    }


    private List<ChatRoom> createSampleChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        // Create and populate one or more ChatRoom objects
        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setRoomId("Room1");
        chatRoom1.setStartTimeUtc("2023-10-18T12:00:00");
        // Add chatRoom1 to the list
        chatRooms.add(chatRoom1);
        return chatRooms;
    }


}
