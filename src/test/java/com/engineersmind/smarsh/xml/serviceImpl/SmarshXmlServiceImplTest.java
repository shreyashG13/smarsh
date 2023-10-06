package com.engineersmind.smarsh.xml.serviceImpl;

import com.engineersmind.smarsh.xml.model.Action;
import com.engineersmind.smarsh.xml.model.ChatRoom;
import com.engineersmind.smarsh.xml.model.Participant;
import com.engineersmind.smarsh.xml.model.Root;
import com.engineersmind.smarsh.xml.service.SmarshXmlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SmarshXmlServiceImplTest {

    @InjectMocks
    private SmarshXmlServiceImpl smarshXmlService;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetApiRequest() throws IOException, ParseException {
        // Create a mock response object
        Root mockRoot = createMockRoot(); // Create a mock Root object
        ResponseEntity<Root> mockResponse = ResponseEntity.ok(mockRoot);

        // Mock the behavior of restTemplate.exchange method
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Root.class)))
                .thenReturn(mockResponse);

        // Call the service method
        ResponseEntity<?> responseEntity = smarshXmlService.getApiRequest();

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());

    }

    private Root createMockRoot() {
        Root root = new Root();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId("123");
        chatRoom.setStartTimeUtc("2023-09-25T12:00:00Z");

        Participant participant = new Participant();
        participant.setLoginName("user1");

        Action action = new Action();
        action.setType("Invite");
        action.setInviterLoginName("admin");
        action.setLoginName("user1");
        action.setDateTimeUTC("2023-09-25T12:05:00Z");
        action.setContent("Invitation content");

        List<Action> actions = new ArrayList<>();
        actions.add(action);

        participant.setActions((ArrayList<Action>) actions);

        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        chatRoom.setParticipants((ArrayList<Participant>) participants);

        List<ChatRoom> chatRooms = new ArrayList<>();
        chatRooms.add(chatRoom);

        root.setChatRooms((ArrayList<ChatRoom>) chatRooms);

        return root;
    }
}
