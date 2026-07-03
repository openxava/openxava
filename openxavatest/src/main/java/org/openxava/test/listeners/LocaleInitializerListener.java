package org.openxava.test.listeners;

import java.util.Locale;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Sets the default locale to US so the test suite behaves the same
 * regardless of the machine's OS locale. Needed when the application
 * is deployed on an external Tomcat (startup.bat), since in that case
 * the Spring Boot launcher's main method is not executed.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@WebListener
public class LocaleInitializerListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
		Locale.setDefault(Locale.US);
	}

}
