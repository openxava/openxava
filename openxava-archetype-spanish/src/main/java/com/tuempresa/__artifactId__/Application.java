package com.tuempresa.__artifactId__;

import org.openxava.util.DBServer;
import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para arrancar la aplicación.
 *
 * Ejecútala directamente desde tu IDE o usa
 * <code>mvn spring-boot:run</code> desde la línea de órdenes.
 */
@SpringBootApplication
public class Application extends OpenXavaApplication {

	public static void main(String[] args) throws Exception {
		DBServer.start("tuaplicacion-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/resources/application.properties
		SpringApplication.run(Application.class, args);
	}

}
