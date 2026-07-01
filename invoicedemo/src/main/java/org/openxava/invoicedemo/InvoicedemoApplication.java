package org.openxava.invoicedemo;

import org.openxava.util.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.openxava.spring.OpenXavaApplication;

/**
 * Spring Boot application class to launch invoicedemo with Spring Boot 4.1.
 * 
 * @since 8.0
 */
@SpringBootApplication
public class InvoicedemoApplication extends OpenXavaApplication {

	/**
	 * @since 8.0
	 */
	public static void main(String[] args) throws Exception {
		DBServer.start("invoicedemo-db");
		SpringApplication.run(InvoicedemoApplication.class, args);
	}

}
