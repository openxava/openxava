package com.yourcompany.yourapp;

import org.openxava.util.DBServer;
import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application class to launch yourapp with Spring Boot.
 *
 * To start the application use: mvn spring-boot:run
 */
@SpringBootApplication
public class Application extends OpenXavaApplication {

	public static void main(String[] args) throws Exception {
		DBServer.start("yourapp-db"); // To use your own database comment this line and configure src/main/resources/application.properties
		SpringApplication.run(Application.class, args);
	}

}
