package org.openxava.chattest;

import org.openxava.util.DBServer;
import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChattestApplication extends OpenXavaApplication {

	public static void main(String[] args) throws Exception {
		DBServer.start("chattest-db"); // To use your own database comment this line and configure application.properties
		SpringApplication.run(ChattestApplication.class, args);
	}

}
