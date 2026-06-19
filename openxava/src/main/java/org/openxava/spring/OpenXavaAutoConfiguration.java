package org.openxava.spring;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.openxava.chat.ChatEndpoint;
import org.openxava.util.DataSourceConnectionProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Spring Boot auto-configuration for OpenXava.
 * <p>
 * Registers servlet components (via {@code @ServletComponentScan}), the default
 * view controller for the root path, the WebSocket endpoint for chat, and 
 * if a Spring-managed {@link DataSource} is present with an embedded Tomcat
 * container, registers the DataSource in the Tomcat naming context under the
 * JNDI name specified in <code>persistence.xml</code>.
 * This ensures that both JPA/Hibernate and OpenXava JDBC utilities can resolve it.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@AutoConfiguration
@ConditionalOnClass({ Tomcat.class, TomcatServletWebServerFactory.class })
@ServletComponentScan(basePackages = { "org.openxava", "com.openxava" })
public class OpenXavaAutoConfiguration implements WebMvcConfigurer {

	/**
	 * @since 8.0
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.jsp");
	}

	/**
	 * @since 8.0
	 */
	@Bean
	@ConditionalOnClass(ServerEndpointExporter.class)
	@ConditionalOnMissingBean
	public ServerEndpointExporter serverEndpointExporter() {
		ServerEndpointExporter exporter = new ServerEndpointExporter();
		exporter.setAnnotatedEndpointClasses(ChatEndpoint.class);
		return exporter;
	}

	/**
	 * @since 8.0
	 */
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> openXavaTomcatCustomizer(
			ObjectProvider<DataSource> dataSourceProvider) {
		return factory -> {
			// SameSite=Lax for all cookies, to pass the ZAP test (OWASP CSRF)
			// "Strict" does not work with Azure AD and "None" does not work with Chrome
			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();
				processor.setSameSiteCookies("Lax");
				((StandardContext) context).setCookieProcessor(processor);
			});

			String jndiName = DataSourceConnectionProvider.getDefaultCleanJPADataSourceName();
			if (jndiName == null) return;

			DataSource dataSource = dataSourceProvider.getIfAvailable();
			if (dataSource != null) {
				DataSourceJndiFactory.setDataSource(dataSource);
			}

			// Enable JNDI in Tomcat (equivalent to tomcat.enableNaming())
			System.setProperty("catalina.useNaming", "true");
			String urlPkgs = System.getProperty("java.naming.factory.url.pkgs");
			String pkgs = "org.apache.naming";
			if (urlPkgs != null && !urlPkgs.contains(pkgs)) {
				pkgs = pkgs + ":" + urlPkgs;
			}
			else if (urlPkgs != null) {
				pkgs = urlPkgs;
			}
			System.setProperty("java.naming.factory.url.pkgs", pkgs);
			if (System.getProperty("java.naming.factory.initial") == null) {
				System.setProperty("java.naming.factory.initial",
					"org.apache.naming.java.javaURLContextFactory");
			}
			factory.addContextLifecycleListeners(new NamingContextListener());
			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				ContextResource resource = new ContextResource();
				resource.setName(jndiName);
				resource.setType("javax.sql.DataSource");
				resource.setProperty("factory", DataSourceJndiFactory.class.getName());
				resource.setSingleton(true);
				context.getNamingResources().addResource(resource);
			});
		};
	}

}
