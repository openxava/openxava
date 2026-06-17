package org.openxava.invoicedemo;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

/**
 * JNDI {@link ObjectFactory} that returns the {@link DataSource} created and
 * managed by Spring Boot (the HikariCP pool configured with
 * <code>spring.datasource.*</code> in application.properties).
 * <p>
 * It is registered in the embedded Tomcat naming context under the JNDI name
 * expected by OpenXava (<code>jdbc/invoicedemoDS</code>), so both Hibernate
 * (via <code>non-jta-data-source</code> in persistence.xml) and OpenXava's
 * {@code DataSourceConnectionProvider} resolve the very same Spring-managed pool.
 *
 * @since 8.0
 */
public class SpringDataSourceJndiFactory implements ObjectFactory {

	private static volatile DataSource dataSource;

	/**
	 * @since 8.0
	 */
	public static void setDataSource(DataSource dataSource) {
		SpringDataSourceJndiFactory.dataSource = dataSource;
	}

	/**
	 * @since 8.0
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
		return dataSource;
	}

}
