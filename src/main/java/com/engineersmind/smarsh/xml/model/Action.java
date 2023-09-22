package com.engineersmind.smarsh.xml.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {
	 @JsonProperty("Type") 
	    public String type;

		@JsonProperty("DateTimeUTC") 
	    public String dateTimeUTC;
	    @JsonProperty("LoginName") 
	    public String loginName;
	    @JsonProperty("Content") 
	    public String content;
	    @JsonProperty("InviterLoginName") 
	    public String inviterLoginName;
	    String userFileName;
	    String fileName;
	    String startedDateTimeUTC;
	    String endDateTimeUTC;
	    String corporateEmailId;
	    String status;
	    
	    
	    public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		
	    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateTimeUTC() {
		return dateTimeUTC;
	}

	public void setDateTimeUTC(String dateTimeUTC) {
		this.dateTimeUTC = dateTimeUTC;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInviterLoginName() {
		return inviterLoginName;
	}

	public void setInviterLoginName(String inviterLoginName) {
		this.inviterLoginName = inviterLoginName;
	}

	public String getUserFileName() {
		return userFileName;
	}

	public void setUserFileName(String userFileName) {
		this.userFileName = userFileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStartedDateTimeUTC() {
		return startedDateTimeUTC;
	}

	public void setStartedDateTimeUTC(String startedDateTimeUTC) {
		this.startedDateTimeUTC = startedDateTimeUTC;
	}

	public String getEndDateTimeUTC() {
		return endDateTimeUTC;
	}

	public void setEndDateTimeUTC(String endDateTimeUTC) {
		this.endDateTimeUTC = endDateTimeUTC;
	}

	public String getCorporateEmailId() {
		return corporateEmailId;
	}

	public void setCorporateEmailId(String corporateEmailId) {
		this.corporateEmailId = corporateEmailId;
	}

	    
}
