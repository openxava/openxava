package org.openxava.spring;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.servlet.ServletContext;

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
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
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
 * if a Spring-managed {@link DataSource} is present, registers it in the Tomcat
 * naming context under the JNDI name specified in <code>persistence.xml</code>,
 * regardless of whether Tomcat is embedded (running with {@code spring-boot:run}
 * or the main class) or external (the app deployed as a WAR on a standalone
 * Tomcat). This ensures that both JPA/Hibernate and OpenXava JDBC utilities can
 * resolve it, with the datasource configuration declared only once, in
 * <code>application.properties</code>.
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
	 * Prepares the embedded Tomcat container so that JNDI resources can be
	 * registered, and registers the datasources in the Tomcat naming context
	 * during context customization (before filter initialization). This is
	 * critical for embedded Tomcat, where filters (e.g. NaviOXFilter) may
	 * trigger JPA/Hibernate initialization during their {@code init()} method,
	 * before {@code ContextRefreshedEvent} fires.
	 * <p>
	 * For external Tomcat (WAR deployment), where context customizers do not
	 * run, the datasource registration is done by {@link #openXavaJndiRegistrar}
	 * on {@code ContextRefreshedEvent}.
	 *
	 * @since 8.0
	 */
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> openXavaTomcatCustomizer(
			OpenXavaDataSourcesProperties dataSourcesProperties,
			ObjectProvider<DataSource> dataSourceProvider) {
		return factory -> {
			// SameSite=Lax for all cookies, to pass the ZAP test (OWASP CSRF)
			// "Strict" does not work with Azure AD and "None" does not work with Chrome
			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();
				processor.setSameSiteCookies("Lax");
				((StandardContext) context).setCookieProcessor(processor);
				context.addWelcomeFile("index.jsp");
			});

			if (!hasJndiDataSources(dataSourcesProperties)) return;

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

			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				String defaultJndiName = DataSourceConnectionProvider.getDefaultCleanJPADataSourceName();
				Map<String, DataSource> additionalDataSources = createAdditionalDataSources(dataSourcesProperties);
				if (defaultJndiName == null && additionalDataSources.isEmpty()) return;

				if (defaultJndiName != null) {
					DataSource dataSource = dataSourceProvider.getIfAvailable();
					if (dataSource != null) {
						DataSourceJndiFactory.setDataSource(dataSource);
					}
				}

				if (defaultJndiName != null) registerJndiResource(context, defaultJndiName);
				for (String jndiName : additionalDataSources.keySet()) {
					registerJndiResource(context, jndiName);
				}
			});

			factory.addContextLifecycleListeners(new NamingContextListener());
		};
	}

	/**
	 * Creates the Spring-managed datasources declared for JNDI (the default
	 * one plus any declared with the <code>openxava.datasources.*</code>
	 * prefix) and registers them in the Tomcat naming context, both when
	 * Tomcat is embedded and when the application is deployed as a WAR on an
	 * external Tomcat. This way the datasource configuration only needs to be
	 * declared once, in <code>application.properties</code>.
	 *
	 * @since 8.0
	 */
	@Bean
	public ApplicationListener<ContextRefreshedEvent> openXavaJndiRegistrar(
			ObjectProvider<DataSource> dataSourceProvider,
			OpenXavaDataSourcesProperties dataSourcesProperties,
			ObjectProvider<ServletContext> servletContextProvider) {
		return event -> {
			String defaultJndiName = DataSourceConnectionProvider.getDefaultCleanJPADataSourceName();
			Map<String, DataSource> additionalDataSources = createAdditionalDataSources(dataSourcesProperties);
			if (defaultJndiName == null && additionalDataSources.isEmpty()) return;

			if (defaultJndiName != null) {
				DataSource dataSource = dataSourceProvider.getIfAvailable();
				if (dataSource != null) {
					DataSourceJndiFactory.setDataSource(dataSource);
				}
			}

			ServletContext servletContext = servletContextProvider.getIfAvailable();
			Context catalinaContext = servletContext == null ? null : getCatalinaContext(servletContext);
			if (catalinaContext == null) return;

			if (defaultJndiName != null) registerJndiResource(catalinaContext, defaultJndiName);
			for (String jndiName : additionalDataSources.keySet()) {
				registerJndiResource(catalinaContext, jndiName);
			}
		};
	}

	/**
	 * @since 8.0
	 */
	private boolean hasJndiDataSources(OpenXavaDataSourcesProperties properties) {
		if (DataSourceConnectionProvider.getDefaultCleanJPADataSourceName() != null) return true;
		for (DataSourceDefinition definition : properties.getDatasources().values()) {
			if (!Is.emptyString(definition.getJndi())) return true;
		}
		return false;
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
			if (DataSourceJndiFactory.isRegistered(definition.getJndi())) continue;
			DataSource dataSource = createDataSource(definition);
			DataSourceJndiFactory.register(definition.getJndi(), dataSource);
			result.put(definition.getJndi(), dataSource);
		}
		return result;
	}

	/**
	 * Obtains the Catalina {@link Context} (embedded or from an external
	 * Tomcat) behind the given {@link ServletContext}, so a JNDI resource can
	 * be registered in it dynamically. Returns {@code null} if the container
	 * is not Tomcat (e.g. another Jakarta EE 11 server), in which case the
	 * JNDI datasource must be declared in that server's own configuration.
	 *
	 * @since 8.0
	 */
	private Context getCatalinaContext(ServletContext servletContext) {
		try {
			Field facadeField = servletContext.getClass().getDeclaredField("context");
			facadeField.setAccessible(true);
			Object applicationContext = facadeField.get(servletContext);
			Field contextField = applicationContext.getClass().getDeclaredField("context");
			contextField.setAccessible(true);
			return (Context) contextField.get(applicationContext);
		}
		catch (Exception ex) {
			return null;
		}
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
