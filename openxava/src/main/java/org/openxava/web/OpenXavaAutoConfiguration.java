package org.openxava.web;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.openxava.util.DataSourceConnectionProvider;
import org.openxava.util.SpringDataSourceJndiFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.tomcat.TomcatWebServer;
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
@ConditionalOnBean(DataSource.class)
public class OpenXavaAutoConfiguration {

	/**
	 * @since 8.0
	 */
	@Bean
	public TomcatServletWebServerFactory openXavaTomcatFactory(DataSource dataSource) {
		String jndiName = DataSourceConnectionProvider.getCleanJPADataSourceName();
		if (jndiName == null) {
			return new TomcatServletWebServerFactory();
		}

		SpringDataSourceJndiFactory.setDataSource(dataSource);

		return new TomcatServletWebServerFactory() {
			
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				ContextResource resource = new ContextResource();
				resource.setName(jndiName);
				resource.setType("javax.sql.DataSource");
				resource.setProperty("factory", SpringDataSourceJndiFactory.class.getName());
				resource.setSingleton(true);
				context.getNamingResources().addResource(resource);
			}
		};
	}

}
