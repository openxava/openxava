package org.openxava.spring;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Base class for OpenXava Spring Boot applications.
 * <p>
 * Eliminates the boilerplate of overriding {@link #configure(SpringApplicationBuilder)}
 * in every application class. Subclasses just need to extend this class and
 * the WAR deployment will work automatically.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class OpenXavaApplication extends SpringBootServletInitializer {

	/**
	 * @since 8.0
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(getClass());
	}

}
