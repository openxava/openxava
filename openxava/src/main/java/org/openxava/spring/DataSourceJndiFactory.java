package org.openxava.spring;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

/**
 * JNDI {@link ObjectFactory} that returns the {@link DataSource}s created and
 * managed by Spring Boot.
 * <p>
 * It is registered in the embedded Tomcat naming context under the JNDI names
 * expected by OpenXava, so both Hibernate and OpenXava's
 * {@code DataSourceConnectionProvider} resolve the very same Spring-managed pools.
 * <p>
 * It supports a default datasource (the one bound to the default persistence
 * unit) plus any number of additional datasources registered by their JNDI name
 * (declared in <code>application.properties</code> with the
 * <code>openxava.datasources.*</code> prefix).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class DataSourceJndiFactory implements ObjectFactory {

	private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
	private static volatile DataSource defaultDataSource;

	/**
	 * Sets the default datasource, returned when the looked up JNDI name has not
	 * been explicitly registered with {@link #register(String, DataSource)}.
	 *
	 * @since 8.0
	 */
	public static void setDataSource(DataSource dataSource) {
		DataSourceJndiFactory.defaultDataSource = dataSource;
	}

	/**
	 * Registers a datasource under the given JNDI name.
	 *
	 * @since 8.0
	 */
	public static void register(String jndiName, DataSource dataSource) {
		dataSources.put(jndiName, dataSource);
	}

	/**
	 * @since 8.0
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
		if (name != null) {
			DataSource dataSource = dataSources.get(name.toString());
			if (dataSource != null) return dataSource;
		}
		return defaultDataSource;
	}

}
