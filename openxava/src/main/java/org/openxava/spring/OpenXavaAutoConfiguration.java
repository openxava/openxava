package org.openxava.spring;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.openxava.chat.ChatEndpoint;
import org.openxava.spring.OpenXavaDataSourcesProperties.DataSourceDefinition;
import org.openxava.util.DataSourceConnectionProvider;
import org.openxava.util.Is;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Spring Boot auto-configuration for OpenXava.
 * <p>
 * Registers servlet components (via {@code @ServletComponentScan}), the default
 * view controller for the root path, the WebSocket endpoint for chat, and 
 * if a Spring-managed {@link DataSource} is present with an embedded Tomcat
 * container, registers the DataSource in the Tomcat naming context under the
 * JNDI name specified in <code>persistence.xml</code>.
 * This ensures that both JPA/Hibernate and OpenXava JDBC utilities can resolve it.
 * <p>
 * It also creates and registers any additional datasource declared in
 * <code>application.properties</code> with the <code>openxava.datasources.*</code>
 * prefix (see {@link OpenXavaDataSourcesProperties}), binding each one to its JNDI
 * name so secondary persistence units can resolve them too.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@AutoConfiguration
@ConditionalOnClass({ Tomcat.class, TomcatServletWebServerFactory.class })
@EnableConfigurationProperties(OpenXavaDataSourcesProperties.class)
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
			ObjectProvider<DataSource> dataSourceProvider,
			OpenXavaDataSourcesProperties dataSourcesProperties) {
		return factory -> {
			// SameSite=Lax for all cookies, to pass the ZAP test (OWASP CSRF)
			// "Strict" does not work with Azure AD and "None" does not work with Chrome
			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();
				processor.setSameSiteCookies("Lax");
				((StandardContext) context).setCookieProcessor(processor);
			});

			String defaultJndiName = DataSourceConnectionProvider.getDefaultCleanJPADataSourceName();
			if (defaultJndiName != null) {
				DataSource dataSource = dataSourceProvider.getIfAvailable();
				if (dataSource != null) {
					DataSourceJndiFactory.setDataSource(dataSource);
				}
			}

			Map<String, DataSource> additionalDataSources = createAdditionalDataSources(dataSourcesProperties);

			if (defaultJndiName == null && additionalDataSources.isEmpty()) return;

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
				if (defaultJndiName != null) registerJndiResource(context, defaultJndiName);
				for (String jndiName : additionalDataSources.keySet()) {
					registerJndiResource(context, jndiName);
				}
			});
		};
	}

	/**
	 * Builds the additional datasources declared with the
	 * <code>openxava.datasources.*</code> prefix and registers them in
	 * {@link DataSourceJndiFactory} by their JNDI name.
	 *
	 * @since 8.0
	 */
	private Map<String, DataSource> createAdditionalDataSources(OpenXavaDataSourcesProperties properties) {
		Map<String, DataSource> result = new LinkedHashMap<>();
		for (DataSourceDefinition definition : properties.getDatasources().values()) {
			if (Is.emptyString(definition.getJndi())) continue;
			DataSource dataSource = createDataSource(definition);
			DataSourceJndiFactory.register(definition.getJndi(), dataSource);
			result.put(definition.getJndi(), dataSource);
		}
		return result;
	}

	/**
	 * @since 8.0
	 */
	private DataSource createDataSource(DataSourceDefinition definition) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(definition.getUrl());
		if (!Is.emptyString(definition.getDriverClassName())) {
			config.setDriverClassName(definition.getDriverClassName());
		}
		config.setUsername(definition.getUsername());
		config.setPassword(definition.getPassword());
		if (definition.getMaximumPoolSize() != null) config.setMaximumPoolSize(definition.getMaximumPoolSize());
		if (definition.getMinimumIdle() != null) config.setMinimumIdle(definition.getMinimumIdle());
		if (definition.getConnectionTimeout() != null) config.setConnectionTimeout(definition.getConnectionTimeout());
		return new HikariDataSource(config);
	}

	/**
	 * @since 8.0
	 */
	private void registerJndiResource(Context context, String jndiName) {
		ContextResource resource = new ContextResource();
		resource.setName(jndiName);
		resource.setType("javax.sql.DataSource");
		resource.setProperty("factory", DataSourceJndiFactory.class.getName());
		resource.setSingleton(true);
		context.getNamingResources().addResource(resource);
	}

}
