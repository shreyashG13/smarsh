package com.engineersmind.smarsh.xml.serviceImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.engineersmind.smarsh.xml.model.*;
import com.engineersmind.smarsh.xml.service.*;
//import com.smarshDumpXml.model.ChatRoom;
//import com.smarshDumpXml.model.Participant;
//import com.smarshDumpXml.model.Root;
//import com.smarshDumpXml.service.SmarshXmlService;
import java.util.concurrent.*;



@Service
public class SmarshXmlServiceImpl implements SmarshXmlService{
	private static final Logger log = LoggerFactory.getLogger( SmarshXmlServiceImpl.class);



	@Override
	public ResponseEntity<?> getApiRequest() throws IOException, ParseException {

		RestTemplate restTemplate=new RestTemplate();
		// Get 10th record data
		URL getUrl = new URL("https://bff.staging.iconnections.io/api/message/history?date=2023-09-15&historicalData=true&taskId=4cabc247-ae3c-4f43-8aba-c5754f3ff075");
		String apiUrl=getUrl.toString();
		// Create HttpHeaders and set the authorization token
		HttpHeaders headers = new HttpHeaders();
		//headers.set("Authorization", "Bearer " + authToken);
		// HttpURLConnection conection = (HttpURLConnection) getUrl.openConnection();
		// Create an HttpEntity with the headers
		HttpEntity<String> entity = new HttpEntity<>(headers);
		// Make the GET request with the entity


		ResponseEntity<Root> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Root.class); 

		Root root =response.getBody();
		//root=validationService.validateXmlField(root);
		return ResponseEntity.ok(creatingXMLwithJson(root));
	}

	/* Start : Adding Code for generating Files in a Date range*/
	private String creatingXMLwithJsonForList(List<Root> roots) {
		StringBuilder combinedXml = new StringBuilder();

		for (Root root : roots) {
			try {
				combinedXml.append(creatingXMLwithJson(root));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} 
		}

		return combinedXml.toString();
	}


	@Override
	public ResponseEntity<?> getApiRequestpastdata(String startDate, String endDate) {
		ExecutorService executor = Executors.newFixedThreadPool(2); // Configuring the thread pool size
		List<Future<Root>> futures = new ArrayList<>();

		LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
		LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

		while (!start.isAfter(end)) {
		    final LocalDate date = start;
		    System.out.println("Submitting task for date: " + date); // Diagnostic log
		    futures.add(executor.submit(() -> {
		        System.out.println("Processing date in thread: " + date); // Diagnostic log inside thread
		        return fetchDailyData(date); // Ensure this method has try-catch and logs
		    }));
		    start = start.plusDays(1);
		}


		List<Root> results = new ArrayList<>();
		for (Future<Root> future : futures) {
			try {
				Root root = future.get(); // Waits for the completion of the task
				if (root != null) {
					results.add(root);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace(); // Handle the exception
			}
		}

		executor.shutdown(); // Initiate shutdown
		try {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Wait for tasks to finish

		return ResponseEntity.ok(creatingXMLwithJsonForList(results));
	}

	private Root fetchDailyData(LocalDate date) {
		RestTemplate restTemplate = new RestTemplate();
		String fullUrl = "https://bff.staging.iconnections.io/api/message/history"
				+ "?date=" + date.toString()
				+ "&historicalData=true"
				+ "&taskId=4cabc247-ae3c-4f43-8aba-c5754f3ff075";
		log.info("fetching data on date" + date.toString());
		HttpHeaders headers = new HttpHeaders();
		//headers.set("Authorization", "Bearer " + authToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<Root> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, Root.class);
			if (response.getBody() == null) {
				System.err.println("No data received for date: " + date);
			}
			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error fetching data for date: " + date + ". Exception: " + e.getMessage());
			return null; // Log and return null
		}
	}
	/*End : Adding Code for generating Files in a Date range*/

	//Xml For chat Interaction
	public static ResponseEntity<?> creatingXMLwithJson(Root requestFileDump1)  throws  IOException, ParseException  {

		List<ChatRoom> chatRoomList=new ArrayList<>();
		Action action1=new Action();
		//to fetch the date from the JSON 
		String dateStr = null;
		String formattedDate = "";
		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'UTC' yyyy", Locale.US);
		SimpleDateFormat outputFormat = new SimpleDateFormat(" MMMM dd yyyy");

		for(ChatRoom c1:requestFileDump1.getChatRooms()) {
			//Fetching Current date from JSON date
			if (dateStr == null) {
				dateStr = c1.getEndTimeUtc();
			}

			if (dateStr != null) {
				try {
					Date cur_date = inputFormat.parse(dateStr);

					formattedDate = outputFormat.format(cur_date); // This will give you the desired format

				} catch (ParseException e) {
					log.error("Failed to parse date: " + dateStr, e);
				}
			} else {
				log.warn("dateStr is null for ChatRoom: " + c1.toString());
			}

			if(c1.getParticipants()!=null&&c1.getStartTimeUtc()!=null
					&&c1.getPerspective()!=null&&c1.getEndTimeUtc()!=null
					&&c1.getEndTimeUtc()!=c1.getStartTimeUtc()) {
				if(c1.getScreenType().contains("Meetup")||c1.getScreenType().contains("Message")) {
					for(Participant p:c1.getParticipants()) {
						if(p.getActions()!=null) {
							for(Action action:p.getActions()) {
								if(action.getType().contains("Message")||action.getType().contains("Invite")||action.getType().contains("FileTransferEnd")) {
									chatRoomList.add(c1);		
								}
							}
						}
					}
				}
			}
		}
		for(ChatRoom c:chatRoomList) {
			Date date = new Date();
			SimpleDateFormat DateFor = new SimpleDateFormat("yyyy MMMM dd");
			String currentDate= DateFor.format(date);
			String stri=changeTimeStamptoSEpoch(c.getStartTimeUtc());
			List<Participant> participantList=new ArrayList<>();
			List<Action>  actionList=new ArrayList<>();
			Participant participant=new Participant();
			String  xmlElementString=new String();
			//Adding Head Elements
			xmlElementString=xmlElementString+"<FileDump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
					+"\n";
			xmlElementString=xmlElementString+"\n"+"<Conversation Perspective=\""+c.getPerspective()+"\">"+
					"\n"+"<RoomId>"+c.getRoomId()+"</RoomId>"
					+"\n"+"<StartTimeUtc>"+stri+"<StartTimeUtc/>"
					//	 				+"\n"+"<CallInitiator>"+c.getCallInitiator()+"</CallInitiator>"
					//	 				+"\n"+"<CallType>"+fileDump.getCallType()+"</CallType>"
					//	 				+"\n"+"<Vendor>"+fileDump.getVendor()+"</Vendore>"
					//	 				+"\n"+"<Network>"+fileDump.getNetwork()+"</Network>"
					//	 				+"\n"+"<Channel>"+fileDump.getChannel()+"</Channel>"
					+"\n";
			if(c.getParticipants().size()>=0) {
				for(int i=0;i<=c.getParticipants().size()-1;i++) {
					participantList.add(c.getParticipants().get(i));
				}
				for(Participant p1:participantList) {
					actionList.addAll(p1.getActions());
					for(Action action:actionList) {
						if(action.getDateTimeUTC()!=null&&action.getType().contains("ParticipantEntered")) {
							p1.setDateTimeUtc(action.getDateTimeUTC());
						}
					}
					participantList.removeAll(p1.getActions());
					xmlElementString= xmlElementString+"\n"+"<ParticipantEntered>"
							+"\n"+"<LoginName>"+p1.getLoginName()+"</LoginName>"
							+"\n"+"<DateTimeUTC>"+p1.getDateTimeUtc()+"</DateTimeUTC>"
							//			      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
							//			      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
							//			      	+"\n"+"<CorporateEmailId>"+p1.getCorporateEmailId()+"<CorporateEmailID>"
							+"\n"+"</ParticipantEntered>"
							+"\n"+"\n";
					action1.setLoginName(p1.getLoginName());

				}
			}
			for(Action action:actionList) {
				String str=changeTimeStamptoSEpoch(action.getDateTimeUTC());
				//Adding element for invite
				if(action.getType().contains("Invite")) {
					str=changeTimeStamptoSEpoch(action.getDateTimeUTC());
					xmlElementString = xmlElementString+"\n"+"<Invite>"
							+"\n"+"<InviterLoginName>"+action.getInviterLoginName()+"</InviterLoginName>"
							+"\n"+"<LoginName>"+action.getLoginName()+"</LoginName>"
							+"\n"+"<DateTimeUTC>"+str+"</DateTimeUTC>"
							//	+"\n"+"<Content>"+action.getContent()+"</Content>"
							// 	+"\n"+"<Base64Content>"+action.getContent()+"<Base64Content/>"
							//  	+"\n"+"<CorporateEmailId>"+p.getCorporateEmailId()+"<CorporateEmailID>"
							+"\n"+"</Invite>"
							+"\n";
				}
				if(action.getType().contains("Message")) {
					//Adding Message Elements
					str=changeTimeStamptoSEpoch(action.getDateTimeUTC());
					xmlElementString= xmlElementString +"<Message>"
							+"\n"+"<LoginName>"+action1.getLoginName()+"</LoginName>"
							+"\n"+"<DateTimeUTC>"+str+"</DateTimeUTC>"
							//			 	      	+"\n"+"<CorporateEmailId>"+action.getCorporateEmailId()+"</CorporateEmailId>"
							+"\n"+"<Content>"+action.getContent()+"</Content>"
							//				 	   	 	      	+"\n"+"<MessageType>"+m.getMessageType()+"</MessageType>"
							//				 	   	 	      	+"\n"+"<MessageId>"+m.getMessageId()+"</MessageId>"
							+"\n"+"</Message>"
							+"\n"+"\n";
				}
				//Adding FiletranferStarted Elements
				if(action.getType().contains("Filetranfer")) {
					str=changeTimeStamptoSEpoch(action.getDateTimeUTC());
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
				//Adding ParticipentLeft Elements	

			}

			for(Participant p2:participantList) {
				for(Action action:actionList) {
					if(action.getDateTimeUTC()!=null&&action.getType().contains("ParticipantLeft")) {
						p2.setDateTimeUtc(action.getDateTimeUTC());
					}
				}
				String str="";
				if(participant.getDateTimeUtc()!=null) {
					str=changeTimeStamptoSEpoch(participant.getDateTimeUtc());
				}

				xmlElementString= xmlElementString +"\n"+"<ParticipantLeft>"
						+"\n"+"<LoginName>"+p2.getLoginName()+"</LoginName>"
						+"\n"+"<DateTimeUTC>"+p2.getDateTimeUtc()+"</DateTimeUTC>"
						//	      	+"\n"+"<InternalFlag>"+pe.getInternalFlag()+"</InternalFlag>"
						//	      	+"\n"+"<ConversationID>"+pe.getConversionId()+"<ConversationID/>"
						//	      	+"\n"+"<CorporateEmailId>"+participant.getCorporateEmailId()+"<CorporateEmailID>"
						+"\n"+"</ParticipantLeft>"
						+"\n"+"\n";
			}
			String str=changeTimeStamptoSEpoch(c.getEndTimeUtc());
			xmlElementString= xmlElementString+"<EndTimeUTC>"+str+"<EndTimeUtc/>"
					+"\n" +"</Conversation>"+"\n"
					+"\n"+"</FileDump>";
			//returning the final created string of Elements
			String finalXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"+xmlElementString ;
			//setiing a current date for creating a folder 
			
			Files.createDirectories(Paths.get("C:\\work\\History\\check\\"+formattedDate+"\\"));
			FileWriter file1 = new FileWriter("C:\\work\\History\\check\\"+formattedDate+"\\"+c.getRoomId()+".xml");
			file1.write(finalXmlString);   
			file1.flush();    
			file1.close();       	
		}	
		log.info("Xml files are generated successfully");
		return ResponseEntity.ok("Your XML data is successfully written into multipleXmlFiles "+""+chatRoomList.size());	   
		// return ResponseEntity.ok(cl);
	}

	//Converting date in epoch form
	public static String changeTimeStamptoSEpoch(String str) throws ParseException {
		// Check if the input string is null
		if (str == null || str.trim().isEmpty()) {
			// Handle the null or empty case appropriately
			// For instance, you might return null or throw a custom exception
			return null; // or throw new IllegalArgumentException("Input date string is null or empty");
		}

		SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
		Date date = parserSDF.parse(str);
		long epoch = date.getTime();
		str = Long.toString(epoch);
		String newDate = str.substring(0, 10);
		return newDate;
	}

}	


