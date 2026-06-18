package org.openxava.spring;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

/**
 * JNDI {@link ObjectFactory} that returns the {@link DataSource} created and
 * managed by Spring Boot.
 * <p>
 * It is registered in the embedded Tomcat naming context under the JNDI name
 * expected by OpenXava, so both Hibernate and OpenXava's
 * {@code DataSourceConnectionProvider} resolve the very same Spring-managed pool.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class DataSourceJndiFactory implements ObjectFactory {

	private static volatile DataSource dataSource;

	/**
	 * @since 8.0
	 */
	public static void setDataSource(DataSource dataSource) {
		DataSourceJndiFactory.dataSource = dataSource;
	}

	/**
	 * @since 8.0
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
		return dataSource;
	}

}
