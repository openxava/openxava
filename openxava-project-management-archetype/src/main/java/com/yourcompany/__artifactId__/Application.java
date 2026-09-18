package com.yourcompany.__artifactId__;

import org.openxava.util.DBServer;
import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

/**
 * Main class to launch the application.
 *
 * Run it directly from your IDE or use
 * <code>mvn spring-boot:run</code> from the command line.
 */
@SpringBootApplication
@ServletComponentScan // To register @WebListener classes like QuartzSchedulerListener
public class Application extends OpenXavaApplication {

	public static void main(String[] args) throws Exception {
		DBServer.start("yourapp-db"); // To use your own database comment this line and configure src/main/resources/application.properties
		SpringApplication.run(Application.class, args);
	}

}
