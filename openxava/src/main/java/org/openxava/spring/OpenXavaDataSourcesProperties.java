package org.openxava.spring;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Declarative definition of the additional datasources of an OpenXava
 * application, bound from <code>application.properties</code> with the
 * <code>openxava.datasources.*</code> prefix.
 * <p>
 * The default datasource is configured the standard Spring Boot way with
 * <code>spring.datasource.*</code>. Any additional datasource (for example the
 * ones used by secondary persistence units in <code>persistence.xml</code>) is
 * declared here, so the whole database configuration stays in a single
 * declarative file without any Java code. Each entry is registered in the
 * embedded Tomcat JNDI context under its <code>jndi</code> name by
 * {@link OpenXavaAutoConfiguration}.
 * <p>
 * Example:
 * <pre>
 * openxava.datasources.companya.jndi=jdbc/companyaDS
 * openxava.datasources.companya.url=jdbc:hsqldb:file:data/company-a-db
 * openxava.datasources.companya.username=sa
 * openxava.datasources.companya.driver-class-name=org.hsqldb.jdbcDriver
 * </pre>
 *
 * @author Javier Paniza
 * @since 8.0
 */
@ConfigurationProperties(prefix = "openxava")
public class OpenXavaDataSourcesProperties {

	private Map<String, DataSourceDefinition> datasources = new LinkedHashMap<>();

	/**
	 * @since 8.0
	 */
	public Map<String, DataSourceDefinition> getDatasources() {
		return datasources;
	}

	/**
	 * @since 8.0
	 */
	public void setDatasources(Map<String, DataSourceDefinition> datasources) {
		this.datasources = datasources;
	}

	/**
	 * Connection settings of a single additional datasource.
	 *
	 * @since 8.0
	 */
	public static class DataSourceDefinition {

		private String jndi;
		private String url;
		private String username;
		private String password;
		private String driverClassName;
		private Integer maximumPoolSize;
		private Integer minimumIdle;
		private Long connectionTimeout;

		public String getJndi() {
			return jndi;
		}

		public void setJndi(String jndi) {
			this.jndi = jndi;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public Integer getMaximumPoolSize() {
			return maximumPoolSize;
		}

		public void setMaximumPoolSize(Integer maximumPoolSize) {
			this.maximumPoolSize = maximumPoolSize;
		}

		public Integer getMinimumIdle() {
			return minimumIdle;
		}

		public void setMinimumIdle(Integer minimumIdle) {
			this.minimumIdle = minimumIdle;
		}

		public Long getConnectionTimeout() {
			return connectionTimeout;
		}

		public void setConnectionTimeout(Long connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
		}

	}

}
