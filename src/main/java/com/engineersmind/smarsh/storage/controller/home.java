package com.engineersmind.smarsh.storage.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class home {
	
	
	@GetMapping("/test")
	public String hometext1() 
	{
		return "test........";
	}
	@GetMapping
	public String hometext() 
	{
		return "checking CI-CD running ....";
	}
}
