package com.LIB.MessagingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 *  @author Abenezer Teshome  - Date 17/aug/2024
 */

@SpringBootApplication
@EnableMongoRepositories
public class MessagingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingSystemApplication.class, args);
	}

}
