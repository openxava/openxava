package org.openxava.test.spring;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Spring configuration for the additional datasources (companyA, companyB)
 * used by openxavatest.
 * <p>
 * These datasources are registered in the embedded Tomcat JNDI context so
 * that Hibernate can resolve them via the JNDI names declared in
 * {@code persistence.xml}.
 *
 * @since 8.0
 */
@Configuration
@ConditionalOnClass({ org.apache.catalina.startup.Tomcat.class, TomcatServletWebServerFactory.class })
public class AdditionalDataSourcesConfiguration {

	private static final String COMPANY_A_JNDI = "jdbc/companyaDS";
	private static final String COMPANY_B_JNDI = "jdbc/companybDS";

	/**
	 * @since 8.0
	 */
	@Bean
	public DataSource companyADataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:hsqldb:file:data/company-a-db");
		config.setDriverClassName("org.hsqldb.jdbcDriver");
		config.setUsername("sa");
		config.setPassword("");
		config.setMaximumPoolSize(20);
		config.setMinimumIdle(5);
		config.setConnectionTimeout(10000);
		config.setPoolName("companyA-pool");
		HikariDataSource ds = new HikariDataSource(config);
		MultiDataSourceJndiFactory.register(COMPANY_A_JNDI, ds);
		return ds;
	}

	/**
	 * @since 8.0
	 */
	@Bean
	public DataSource companyBDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:hsqldb:file:data/company-b-db");
		config.setDriverClassName("org.hsqldb.jdbcDriver");
		config.setUsername("sa");
		config.setPassword("");
		config.setMaximumPoolSize(20);
		config.setMinimumIdle(5);
		config.setConnectionTimeout(10000);
		config.setPoolName("companyB-pool");
		HikariDataSource ds = new HikariDataSource(config);
		MultiDataSourceJndiFactory.register(COMPANY_B_JNDI, ds);
		return ds;
	}

	/**
	 * @since 8.0
	 */
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> additionalDataSourcesTomcatCustomizer() {
		return factory -> {
			factory.addContextCustomizers((TomcatContextCustomizer) context -> {
				registerJndiResource(context, COMPANY_A_JNDI);
				registerJndiResource(context, COMPANY_B_JNDI);
			});
		};
	}

	private void registerJndiResource(Context context, String jndiName) {
		ContextResource resource = new ContextResource();
		resource.setName(jndiName);
		resource.setType("javax.sql.DataSource");
		resource.setProperty("factory", MultiDataSourceJndiFactory.class.getName());
		resource.setSingleton(true);
		context.getNamingResources().addResource(resource);
	}

}
