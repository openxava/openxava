package org.openxava.test.spring;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

/**
 * JNDI {@link ObjectFactory} that returns the appropriate {@link DataSource}
 * based on the JNDI name used in the lookup.
 * <p>
 * Used for the additional datasources (companyA, companyB) in openxavatest.
 *
 * @since 8.0
 */
public class MultiDataSourceJndiFactory implements ObjectFactory {

	private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

	/**
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
		String nameStr = name.toString();
		return dataSources.get(nameStr);
	}

}
