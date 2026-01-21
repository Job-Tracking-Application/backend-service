package com.jobtracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JobTrackingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobTrackingBackendApplication.class, args);
	}

}
