package com.openxava.naviox.web;

import java.util.EnumSet;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for NaviOXFilter registration in Spring Boot applications.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@AutoConfiguration
public class NaviOXAutoConfiguration {

	@Bean
	public FilterRegistrationBean<NaviOXFilter> naviOXFilter() {
		FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new NaviOXFilter());
		registration.setName("naviox");
		registration.addUrlPatterns("*.jsp", "/modules/*", "/phone/index.jsp", "/m/*");
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
		return registration;
	}

}
