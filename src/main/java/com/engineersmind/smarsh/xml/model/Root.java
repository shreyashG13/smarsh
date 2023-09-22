package com.engineersmind.smarsh.xml.model;

import java.util.ArrayList;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
	
	@JsonProperty("ChatRooms") 
    public ArrayList<ChatRoom> chatRooms;

	public ArrayList<ChatRoom> getChatRooms() {
		return chatRooms;
	}

	public void setChatRooms(ArrayList<ChatRoom> chatRooms) {
		this.chatRooms = chatRooms;
	}

}
