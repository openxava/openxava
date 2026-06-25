package org.openxava.test;

import java.util.Locale;

import org.openxava.spring.OpenXavaApplication;
import org.openxava.util.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application class to launch openxavatest with Spring Boot 4.1.
 *
 * @since 8.0
 */
@SpringBootApplication
public class OpenXavaTestApplication extends OpenXavaApplication {

	/**
	 * @since 8.0
	 */
	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.US);
		DBServer.start("openxavatest-db");
		SpringApplication.run(OpenXavaTestApplication.class, args);
	}

}
