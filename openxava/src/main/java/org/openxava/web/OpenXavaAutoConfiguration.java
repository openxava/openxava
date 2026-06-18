package org.openxava.web;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.openxava.util.DataSourceConnectionProvider;
import org.openxava.util.SpringDataSourceJndiFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot Auto-configuration for OpenXava core components.
 * <p>
 * If a Spring-managed {@link DataSource} is present and an embedded Tomcat
 * container is used, this configuration registers the DataSource in the Tomcat
 * naming context under the JNDI name specified in <code>persistence.xml</code>.
 * This ensures that both JPA/Hibernate and OpenXava JDBC utilities can resolve it.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@AutoConfiguration
@ConditionalOnClass({ Tomcat.class, TomcatServletWebServerFactory.class })
public class OpenXavaAutoConfiguration {

	/**
	 * @since 8.0
	 */
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> openXavaTomcatCustomizer(
			ObjectProvider<DataSource> dataSourceProvider) {
		return factory -> {
			String jndiName = DataSourceConnectionProvider.getDefaultCleanJPADataSourceName();
			if (jndiName == null) return;

			DataSource dataSource = dataSourceProvider.getIfAvailable();
			if (dataSource != null) {
				SpringDataSourceJndiFactory.setDataSource(dataSource);
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
				resource.setProperty("factory", SpringDataSourceJndiFactory.class.getName());
				resource.setSingleton(true);
				context.getNamingResources().addResource(resource);
			});
		};
	}

}
