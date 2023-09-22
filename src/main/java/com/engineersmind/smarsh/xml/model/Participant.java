package com.engineersmind.smarsh.xml.model;

import java.util.ArrayList;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant {
	
	@JsonProperty("LoginName") 
    public String loginName;
    @JsonProperty("CorporateEmailId") 
    public String corporateEmailId;
    @JsonProperty("Actions") 
    public ArrayList<Action> actions;
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getCorporateEmailId() {
		return corporateEmailId;
	}
	public void setCorporateEmailId(String corporateEmailId) {
		this.corporateEmailId = corporateEmailId;
	}
	public ArrayList<Action> getActions() {
		return actions;
	}
	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}


}
