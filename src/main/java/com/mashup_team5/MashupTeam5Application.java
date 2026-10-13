package com.mashup_team5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MashupTeam5Application {

	public static void main(String[] args) {
		SpringApplication.run(MashupTeam5Application.class, args);
	}

}
