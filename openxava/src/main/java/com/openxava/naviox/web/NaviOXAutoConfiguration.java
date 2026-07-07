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
	public FilterRegistrationBean<NaviOXFilter> naviOXFilterJSP() {
		FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new NaviOXFilter());
		registration.setName("naviox-jsp");
		registration.addUrlPatterns("*.jsp");
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
		return registration;
	}

	@Bean
	public FilterRegistrationBean<NaviOXFilter> naviOXFilterModules() {
		FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new NaviOXFilter());
		registration.setName("naviox-modules");
		registration.addUrlPatterns("/modules/*", "/phone/index.jsp");
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
		return registration;
	}

	@Bean
	public FilterRegistrationBean<NaviOXFilter> naviOXFilterServlets() {
		FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new NaviOXFilter());
		registration.setName("naviox-servlets");
		registration.addUrlPatterns("/m/*", "/p/*");
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
		return registration;
	}

}
