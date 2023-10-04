package com.engineersmind.smarsh.xml.serviceImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.engineersmind.smarsh.xml.model.*;
import com.engineersmind.smarsh.xml.service.*;
//import com.smarshDumpXml.model.ChatRoom;
//import com.smarshDumpXml.model.Participant;
//import com.smarshDumpXml.model.Root;
//import com.smarshDumpXml.service.SmarshXmlService;


@Service
public class SmarshXmlServiceImpl implements SmarshXmlService{
	private static final Logger log = LoggerFactory.getLogger( SmarshXmlServiceImpl.class);
	
	
	@Override
	public ResponseEntity<?> getApiRequest() throws IOException, ParseException {
		RestTemplate restTemplate=new RestTemplate();
        // Get 10th record data
        URL getUrl = new URL("https://bff.staging.iconnections.io/api/message/history?date=2023-09-25&historicalData=true&taskId=4cabc247-ae3c-4f43-8aba-c5754f3ff075");
        String apiUrl=getUrl.toString();
           // Create HttpHeaders and set the authorization token
            HttpHeaders headers = new HttpHeaders();
            //headers.set("Authorization", "Bearer " + authToken);
           // HttpURLConnection conection = (HttpURLConnection) getUrl.openConnection();
           // Create an HttpEntity with the headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
         // Make the GET request with the entity
            ResponseEntity<Root> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Root.class);    
	      return ResponseEntity.ok( creatingXMLwithJson(response.getBody()));
	    }
	
	//Xml For chat Interaction
	public static ResponseEntity<?> creatingXMLwithJson(Root requestFileDump)  throws  IOException, ParseException  {
		List<ChatRoom> chatRoomList=new ArrayList<>();
		
	for(ChatRoom c1:requestFileDump.getChatRooms()) {
		if(c1.getParticipants()!=null) {
		for(Participant p:c1.getParticipants()) {
		if(p.getActions()!=null) {
			  chatRoomList.add(c1);		
			}
			}
		}
	}
	for(ChatRoom c:chatRoomList) {
		long stri=changeTimeStamptoSEpoch(c.getStartTimeUtc());
		String  xmlElementString=new String();
			//Adding Head Elements
		xmlElementString=xmlElementString+"<FileDump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
	 				+"\n";
	    	xmlElementString=xmlElementString+"\n"+"<Conversation Perspective=\"Abend term Finance disussion group channel\">"+
			"\n"+"<RoomId>"+c.getRoomId()+"</RoomId>"
	 				+"\n"+"<StartTimeUtc>"+stri+"<StartTimeUtc/>"
	 				+"\n"+"<CallInitiator>"+c.getCallInitiator()+"</CallInitiator>"
//	 				+"\n"+"<CallType>"+fileDump.getCallType()+"</CallType>"
//	 				+"\n"+"<Vendor>"+fileDump.getVendor()+"</Vendore>"
//	 				+"\n"+"<Network>"+fileDump.getNetwork()+"</Network>"
//	 				+"\n"+"<Channel>"+fileDump.getChannel()+"</Channel>"
	 				+"\n";
	    	if(c.getParticipants()!=null) {
			for(Participant p:c.getParticipants()) {
				//Adding  ParticipentEnter Elements 
				 xmlElementString= xmlElementString+"\n"+"<ParticipantEntered>"
			      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
//			      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
//			      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
//			      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
			      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
			      	+"\n"+"</ParticipantEntered>"
			      	+"\n"+"\n";
				 if(p.getActions()!=null) {
			for(Action action:p.getActions()) {
				long str=changeTimeStamptoSEpoch(action.getDateTimeUTC());
				   
			  	//Adding element for invite
				if(action.getType().contains("Invite")) {
					 xmlElementString = xmlElementString+"\n"+"<Invite>"
							 +"\n"+"<InviterLoginName>"+action.getInviterLoginName()+"</InviterLoginName>"
				      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				      			+"\n"+"<DateTimeUTC>"+str+"</DateTimeUTC>"
				      	+"\n"+"<Content>"+action.getContent()+"</Content>"
				     // 	+"\n"+"<Base64Content>"+action.getContent()+"<Base64Content/>"
				    //  	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
				      	+"\n"+"</Invite>"
				      	+"\n";
				}
							 if(action.getType().contains("Message")) {
					 //Adding Message Elements
					 xmlElementString= xmlElementString +"<Message>"
			 	      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
			 	      			+"\n"+"<DateTimeUTC>"+str+"</DateTimeUTC>"
			 	      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"</CorporateEmailId>"
			 	      +"\n"+"<Content>"+action.getContent()+"</Content>"
//				 	   	 	      	+"\n"+"<MessageType>"+m.getMessageType()+"</MessageType>"
//				 	   	 	      	+"\n"+"<MessageId>"+m.getMessageId()+"</MessageId>"
				 	   	 	      	+"\n"+"</Message>"
				 	   	 	      	+"\n"+"\n";
				 }
					//Adding FiletranferStarted Elements
					 if(action.getType().contains("Filetranfer")) {
                         xmlElementString= xmlElementString +"<FileTransferStarted>"
				 	      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				 	      			+"\n"+"<DateTimeUTC>"+str+"</DateTimeUTC>"
				 	      	+"\n"+"<UserFileName>"+action.getUserFileName()+"</UserFileName>"
				 	      	+"\n"+"<FileName>"+action.getFileName()+"</FileName>"
				 	      	+"\n"+"<Status>"+action.getStatus()+"</Status>"
				 	      	+"\n"+"</FileTransferStarted>"
				 	      	+"\n"+"\n";
				      	//Adding FiletranferEnd Elements
				     		xmlElementString= xmlElementString +"<FileTransferEnd>"
				 	      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				 	      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
				 	      	+"\n"+"<UserFileName>"+action.getUserFileName()+"</UserFileName>"
				 	      	+"\n"+"<FileName>"+action.getFileName()+"</FileName>"
				 	      	+"\n"+"<Status>"+action.getStatus()+"</Status>"
				 	      	+"\n"+"</FileTransferEnd>"
				 	      	+"\n"+"\n";
					  }	
			}
				//Adding ParticipentLeft Elements			 
				 xmlElementString= xmlElementString +"<ParticipantLeft>"
			      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
			      			//+"\n"+"<DateTimeUTC>"+p.getDateTimeUtc()+"</DateTimeUTC>"
//			      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
//			      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
			      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
			      	+"\n"+"</ParticipantLeft>"
			      	+"\n"+"\n";
					 }
			}
			 xmlElementString= xmlElementString+"<EndTimeUTC>"+"123456"+"<EndTimeUtc/>"
				        +"\n" +"</Conversation>"+"\n"
				        +"\n"+"</FileDump>";
	    	//returning the final created string of Elements
			 String finalXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"+xmlElementString ;
			
			 FileWriter file1 = new FileWriter("C:\\work\\xml\\"+c.getRoomId()+".xml");
		    	file1.write(finalXmlString);   
	        file1.flush();  
	        log.info("------------Your XML data is successfully written into XmlForRoomChatInteractionJson.xml---------");  
	        file1.close();       	
	    	}
		}	
        return ResponseEntity.ok("Your XML data is successfully written into multipleXmlFiles "+""+requestFileDump.getChatRooms().size());	   
		// return ResponseEntity.ok(cl);
	 }
		
	//Xml for group chat interaction
	public static ResponseEntity<?> creatingXMLForGroupChatwithJson(Root requestFileDump)  throws  IOException, ParseException  {
		List<ChatRoom> chatRoomList=new ArrayList<>();
	for(ChatRoom c1:requestFileDump.getChatRooms()) {
		if(c1.getParticipants()!=null) {
		for(Participant p:c1.getParticipants()) {
			if(p.getActions()!=null) {
			  chatRoomList.add(c1);		
			}
			}
		}
	}
for(ChatRoom c:chatRoomList) {
			String  xmlElementString=new String();
			//Adding Head Elements
		xmlElementString=xmlElementString+"<FileDump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
	 				+"\n";
	    	xmlElementString=xmlElementString+"\n"+"<Conversation Perspective=\"Abend term Finance disussion group channel\">"+
			"\n"+"<RoomId>"+c.getRoomId()+"</RoomId>"
	 				+"\n"+"<StartTimeUtc>"+c.getStartTimeUtc()+"<StartTimeUtc/>"
	 				+"\n"+"<CallInitiator>"+c.getCallInitiator()+"</CallInitiator>"
//	 				+"\n"+"<CallType>"+fileDump.getCallType()+"</CallType>"
//	 				+"\n"+"<Vendor>"+fileDump.getVendor()+"</Vendore>"
//	 				+"\n"+"<Network>"+fileDump.getNetwork()+"</Network>"
//	 				+"\n"+"<Channel>"+fileDump.getChannel()+"</Channel>"
	 				+"\n";
	    	if(c.getParticipants()!=null) {
			for(Participant p:c.getParticipants()) {
				//Adding  ParticipentEnter Elements 
				 xmlElementString= xmlElementString+"\n"+"<ParticipantEntered>"
			      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
//			      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
//			      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
//			      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
			      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
			      	+"\n"+"</ParticipantEntered>"
			      	+"\n"+"\n";
				 if(p.getActions()!=null) {
			for(Action action:p.getActions()) {
					//Adding element for invite
				if(action.getType().contains("Invite")) {
					 xmlElementString = xmlElementString+"\n"+"<Invite>"
							 +"\n"+"<InviterLoginName>"+action.getInviterLoginName()+"</InviterLoginName>"
				      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
				      	+"\n"+"<Content>"+action.getContent()+"</Content>"
				     // 	+"\n"+"<Base64Content>"+action.getContent()+"<Base64Content/>"
				    //  	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
				      	+"\n"+"</Invite>"
				      	+"\n";
				}
							 if(action.getType().contains("Message")) {
						
					  //Adding Message Elements
					 xmlElementString= xmlElementString +"<Message>"
			 	      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
			 	      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
			 	      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"</CorporateEmailId>"
			 	      +"\n"+"<Content>"+action.getContent()+"</Content>"
//				 	   	 	      	+"\n"+"<MessageType>"+m.getMessageType()+"</MessageType>"
//				 	   	 	      	+"\n"+"<MessageId>"+m.getMessageId()+"</MessageId>"
				 	   	 	      	+"\n"+"</Message>"
				 	   	 	      	+"\n"+"\n";
				 }
					//Adding FiletranferStarted Elements
					 if(action.getType().contains("Filetranfer")) {
                         xmlElementString= xmlElementString +"<FileTransferStarted>"
				 	      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				 	      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
				 	      	+"\n"+"<UserFileName>"+action.getUserFileName()+"</UserFileName>"
				 	      	+"\n"+"<FileName>"+action.getFileName()+"</FileName>"
				 	      	+"\n"+"<Status>"+action.getStatus()+"</Status>"
				 	      	+"\n"+"</FileTransferStarted>"
				 	      	+"\n"+"\n";
				      		
				      	//Adding FiletranferEnd Elements
				     		xmlElementString= xmlElementString +"<FileTransferEnd>"
				 	      			+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
				 	      			+"\n"+"<DateTimeUTC>"+action.getDateTimeUTC()+"</DateTimeUTC>"
				 	      	+"\n"+"<UserFileName>"+action.getUserFileName()+"</UserFileName>"
				 	      	+"\n"+"<FileName>"+action.getFileName()+"</FileName>"
				 	      	+"\n"+"<Status>"+action.getStatus()+"</Status>"
				 	      	+"\n"+"</FileTransferEnd>"
				 	      	+"\n"+"\n";
					 
					 }	
			}
				//Adding ParticipentLeft Elements			 
				 xmlElementString= xmlElementString +"<ParticipantLeft>"
			      			+"\n"+"<LoginName>"+p.getLoginName()+"</LoginName>"
			      			//+"\n"+"<DateTimeUTC>"+p.getDateTimeUtc()+"</DateTimeUTC>"
//			      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
//			      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
			      	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
			      	+"\n"+"</ParticipantLeft>"
			      	+"\n"+"\n";
					 }
			}
			 xmlElementString= xmlElementString+"<EndTimeUTC>"+"123456"+"<EndTimeUtc/>"
				        +"\n" +"</Conversation>"+"\n"
				        +"\n"+"</FileDump>";
	    	//returning the final created string of Elements
			 String finalXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"+xmlElementString ;
			
			 FileWriter file1 = new FileWriter("C:\\work\\xml\\"+c.getRoomId()+".xml");
		    	file1.write(finalXmlString);   
	        file1.flush();  
	        log.info("------------Your XML data is successfully written into XmlForRoomChatInteractionJson.xml---------");  
	        file1.close();       	
	    	}
		}	
        return ResponseEntity.ok("Your XML data is successfully written into XmlForRoomChatInteractionJson.xml");	   
		// return ResponseEntity.ok(cl);
	 }
	
	 //Converting date in epoch form
	 public static long changeTimeStamptoSEpoch(String str) throws ParseException {
		 SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
		 Date date = parserSDF.parse(str);
		 long epoch = date.getTime();
		 System.out.println(epoch+"----------------");
		 return epoch;
	 }
}	


