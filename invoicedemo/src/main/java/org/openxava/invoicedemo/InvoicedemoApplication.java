package org.openxava.invoicedemo;

import java.util.EnumSet;

import jakarta.servlet.DispatcherType;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.openxava.chat.ChatEndpoint;
import org.openxava.util.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.openxava.naviox.web.NaviOXFilter;

/**
 * Spring Boot application class to launch invoicedemo with Spring Boot 4.1.
 * 
 * @since 8.0
 */
@SpringBootApplication
@ServletComponentScan(basePackages = { "org.openxava", "com.openxava" })
public class InvoicedemoApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	/**
	 * @since 8.0
	 */
	public static void main(String[] args) throws Exception {
		DBServer.start("invoicedemo-db");
		SpringApplication.run(InvoicedemoApplication.class, args);
	}

	/**
	 * @since 8.0
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(InvoicedemoApplication.class);
	}

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
	public FilterRegistrationBean<NaviOXFilter> naviOXFilter() {
		FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new NaviOXFilter());
		registration.setName("naviox");
		registration.addUrlPatterns("*.jsp", "/modules/*", "/phone/index.jsp", "/m/*");
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
		return registration;
	}

	/**
	 * @since 8.0
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		ServerEndpointExporter exporter = new ServerEndpointExporter();
		exporter.setAnnotatedEndpointClasses(ChatEndpoint.class);
		return exporter;
	}

	/**
	 * @since 8.0
	 */
	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		return new TomcatServletWebServerFactory() {
			
			/**
			 * @since 8.0
			 */
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}

			/**
			 * @since 8.0
			 */
			@Override
			protected void postProcessContext(Context context) {
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/invoicedemoDS");
				resource.setType("javax.sql.DataSource");
				resource.setProperty("driverClassName", "org.hsqldb.jdbcDriver");
				resource.setProperty("url", "jdbc:hsqldb:hsql://localhost:1666");
				resource.setProperty("username", "sa");
				resource.setProperty("password", "");
				resource.setProperty("maxTotal", "20");
				resource.setProperty("maxIdle", "5");
				resource.setProperty("maxWaitMillis", "10000");
				context.getNamingResources().addResource(resource);
			}
		};
	}

}
