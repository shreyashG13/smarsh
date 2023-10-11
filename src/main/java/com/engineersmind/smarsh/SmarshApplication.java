package com.engineersmind.smarsh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class SmarshApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarshApplication.class, args);
	}

}
