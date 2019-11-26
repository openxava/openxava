package org.openxava.jpa;


import java.net.*;
import java.util.*;

import javax.persistence.*;
import javax.xml.parsers.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.openxava.component.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.impl.*;
import org.openxava.util.*;
import org.w3c.dom.*;

/**
 * Allows to work easily with EJB3 JPA inside OpenXava applications. <p>
 * 
 * You can use this class in any part of your OpenXava application:
 * calculators, validators, actions, junit tests, etc.<br>
 * To use JPA with this class you can write something as:
 * <pre>
 * Invoice invoice = new Invoice();
 * XPersistence.getManager().persist(invoice);
 * </pre>
 * 
 * And no more.<br>
 * The method {@link getManager()} creates a session and transaction the first time 
 * in the thread, and the OpenXava close the manager at the end of action execution.<br>
 * Also the next code is legal:
 * <pre>
 * Invoice invoice = new Invoice();
 * XPersistence.getManager().persist(invoice);
 * XPersistence.commit(); // In this way you commit and close manually.
 * ...
 * XPersistence.getManager().persist(customer); // As manager has been closed, a new one is created
 * </pre> 
 * 
 * <b>Per thread configuration</b><br>
 * XPersistence allows you to change the configuration in runtime affecting only
 * to the current thread of execution.<br>
 * For example, if you want to have two configuration: 'config1' and 'config2' in
 * your persistence.xml, and you want to change from one to other, it's easy:
 * <pre>
 * XPersistence.setPersistenceUnit("config1");
 * XPersistence.getManager().persist(invoice); // Using config1
 * ...
 * XPersistence.commit();
 * XPersistence.setPersistenceUnit("config2");
 * XPersistence.getManager().persist(invoice); // Using config2
 * ...
 * XPersistence.commit();
 * </pre>
 * And this change only affect to the current execution thread.<br>
 * Or if you want to change the default schema you can write:
 * <pre>
 * XPersistence.setDefaultSchema("COMPANY1");
 * XPersistence.getManager().persist(invoice); // Save in INVOICE table of COMPANY1 Schema
 * ...
 * XPersistence.commit();
 * XPersistence.setDefaultSchema("COMPANY2");
 * XPersistence.getManager().persist(invoice); // Save in INVOICE table of COMPANY2 Schema
 * ...
 * XPersistence.commit();  
 * </pre>
 * Also this only affect to the current thread.<br>
 * 
 * The method {@link #setPersistenceUnitProperties} can be use in the same way
 * that {@link #setPersistenceUnit} and {@link #setDefaultSchema}. <br>    
 * 
 * @author Javier Paniza
 */

public class XPersistence {
	
	private static Log log = LogFactory.getLog(XPersistence.class);

	private final static String XAVA_PERSISTENCE_UNIT_KEY = "xava.persistenceUnit";
	final private static ThreadLocal currentManager = new ThreadLocal();
	private static Map entityManagerFactories = new HashMap();
	final private static ThreadLocal currentPersistenceUnitProperties = new ThreadLocal();
	private static Map defaultPersistenceUnitProperties;
	private static boolean hibernateEventsRegistered = false;

	/**
	 * <code>EntityManager</code> associated to current thread. <p>
	 * 
	 * If no manager exists or it's closed, then create a new one, and start a transaction.<br>
	 *
	 * @return Not null
	 */
	public static EntityManager getManager() {
		EntityManager s = (EntityManager) currentManager.get();
		if (s == null || !s.isOpen()) {
			s = openManager();
		}	
		return s;
	}
	
	/**
	 * Create a new <code>EntityManager</code>. <p>
	 * 
	 * This manager is not associated with the current thread,
	 * and no transaction is started.
	 *
	 * @return Not null
	 */
	public static EntityManager createManager() {
		EntityManager m = getEntityManagerFactory().createEntityManager();
		registerHibernateEvents(m); 
		return new EntityManagerDecorator(m);
	}
				
	private static void registerHibernateEvents(EntityManager m) { 
		if (hibernateEventsRegistered) return;
		Collection<MetaComponent> components = MetaComponent.getAllLoaded();
		if (components.isEmpty()) return;
		if (!components.iterator().next().getMetaEntity().isAnnotatedEJB3()) {			
			XHibernate._registerEvents(((Session) m.getDelegate()).getSessionFactory());
		}
		hibernateEventsRegistered = true;
	}
	
	
	private static EntityManager openManager() {
		EntityManager m = createManager();
		m.getTransaction().begin();
		currentManager.set(m);
		return m;
	}
		
	/**
	 * Commits changes and closes the <code>EntityManager</code> associated to 
	 * the current thread. <p>
	 * 
	 * If no manager or it is closed this method does nothing.<br>
	 * In most cases this method is called by OpenXava automatically, 
	 * hence the programmer that uses JPA APIs does not need to call it.
	 */
	public static void commit() {
		// If manager does not exist then we do not cast to EntityManager,
		// in this way in order to support jdk1.3 when ejb3 is not used.
		Object o = currentManager.get();		
		if (o == null) return;
		EntityManager m = (EntityManager) o;
		if (m.isOpen()) {			
			EntityTransaction t = (EntityTransaction) m.getTransaction();
			try {
				if (t.isActive()) {
					boolean rollbackOnly = t.getRollbackOnly();
					t.commit();
					if (rollbackOnly) throw new RollbackException(XavaResources.getString("transaction_marked_rollbackOnly")); // Because it's not automatic with Hibernate 5.3.7 
				}
				else m.flush();
			}
			finally {
				currentManager.set(null);
				m.close();				
			}
		}
		else {					
			currentManager.set(null);
		}
	}
	
	/**
	 * Rollback changes and closes the <code>EntityManager</code> associated to 
	 * the current thread. <p>
	 * 
	 * If no manager or it is closed this method does nothing.<br>
	 */
	public static void rollback() {
		// If manager does not exist then we do not cast to EntityManager,
		// in this way in order to support jdk1.3 when ejb3 is not used.
		Object o = currentManager.get(); 
		if (o == null) return;
		EntityManager m = (EntityManager) o;
		if (m.isOpen()) {
			EntityTransaction t = (EntityTransaction) m.getTransaction();
			try {
				t.rollback();
			}
			finally {
				currentManager.set(null);
				m.close();
			}
		}					
		else {
			currentManager.set(null);
		}
	}	
	
	private static EntityManagerFactory getEntityManagerFactory() {	
		Map properties = getPersistenceUnitProperties();
		EntityManagerFactory entityManagerFactory = (EntityManagerFactory) 
			entityManagerFactories.get(properties); 
		if (entityManagerFactory == null) {
			try {
				Map factoryProperties = properties;
				if (PersistenceXml.getPropetyValue(getPersistenceUnit(), "hibernate.implicit_naming_strategy") == null) { 
					factoryProperties = new HashMap(properties);
					factoryProperties.put("hibernate.implicit_naming_strategy", "legacy-jpa"); 
				}
				entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnit(), factoryProperties);
			}
			catch (NoSuchFieldError ex) {
				log.error(XavaResources.getString("incorrect_openxava_upgrade")); 
				throw ex;
			}
			catch (ParserConfigurationException ex) {
				log.error(XavaResources.getString("incorrect_openxava_upgrade"));
				throw new RuntimeException(ex);
			}			
			entityManagerFactories.put(new HashMap(properties), entityManagerFactory);			
		}
		return entityManagerFactory;
	}	
	
	/**
	 * @since 5.7.1
	 */
	public static void resetEntityManagerFactory() { 
		Map properties = getPersistenceUnitProperties();
		EntityManagerFactory factory = (EntityManagerFactory) 
			entityManagerFactories.remove(properties);
		if (factory != null) factory.close();
	}

	/**
	 * The name of persistence unit in persistence.xml file. <p>
	 * 
	 * By default is <code>default</code>. <br> 
	 * The value of this properties may be different for each thread.<br> 
	 */
	public static String getPersistenceUnit() {
		return (String) getPersistenceUnitProperties().get(XAVA_PERSISTENCE_UNIT_KEY);
	}

	/**
	 * The name of persistence unit in persistence.xml file. <p>
	 * 
	 * By default is <code>default</code>. <br>
	 * If you change this value it only take effect for the current thread.<p>
	 * If you sent a <code>null</null> then <code>default</code> is assumed. 
	 */	
	public static void setPersistenceUnit(String persistenceUnitName) {		
		if (Is.emptyString(persistenceUnitName)) persistenceUnitName = XavaPreferences.getInstance().getDefaultPersistenceUnit(); 
		Map properties = new HashMap(); 
		properties.put(XAVA_PERSISTENCE_UNIT_KEY, persistenceUnitName);
		currentPersistenceUnitProperties.set(properties);
		String defaultSchema = obtainDefaultSchemaFromPersistenceXML();
		if (defaultSchema != null) {
			properties.put(getHibernateDefaultSchemaPropertyName(), defaultSchema); 
		}		
	}
	
	private static String getHibernateDefaultSchemaPropertyName() { 
		Map<String, Object> properties = getEntityManagerFactory().getProperties();
		String dialect = (String) properties.get("hibernate.dialect");
		if (dialect != null && dialect.toLowerCase().contains("mysql")) return "hibernate.default_catalog";
		if (!Is.emptyString((String)properties.get("hibernate.default_catalog"))) return "hibernate.default_catalog";
		return "hibernate.default_schema";
	}
	
	/**
	 * The properties sent to Persistence (a JPA class) in order to create a 
	 * EntityManagerFactory. <p>
	 * 
	 * The value can be different for each execution thread.<br>
	 *  
	 * @return Not null
	 */
	public static Map getPersistenceUnitProperties() { 
		Map result = (Map) currentPersistenceUnitProperties.get();		
		if (result == null) return getDefaultPersistenceUnitProperties();
		return result;
	}

	private static Map getDefaultPersistenceUnitProperties() {
		if (defaultPersistenceUnitProperties == null) {
			defaultPersistenceUnitProperties = new HashMap();
			defaultPersistenceUnitProperties.put(XAVA_PERSISTENCE_UNIT_KEY, XavaPreferences.getInstance().getDefaultPersistenceUnit());
			String defaultSchema = obtainDefaultSchemaFromPersistenceXML();
			if (defaultSchema != null) {
				defaultPersistenceUnitProperties.put(getHibernateDefaultSchemaPropertyName(), defaultSchema); 
			}
			defaultPersistenceUnitProperties = Collections.unmodifiableMap(defaultPersistenceUnitProperties);
		}
		return defaultPersistenceUnitProperties;
	}

	/**
	 * Set the properties to send to Persistence (a JPA class) in order to create a 
	 * EntityManagerFactory. <p>
	 * 
	 * This only apply to the current execution thread.<br>
	 *  
	 * @return Not null
	 */
	public static void setPersistenceUnitProperties(Map persistenceUnitProperties) { 
		if (persistenceUnitProperties == null) persistenceUnitProperties = new HashMap();
		persistenceUnitProperties.put(XAVA_PERSISTENCE_UNIT_KEY, getPersistenceUnit());
		currentPersistenceUnitProperties.set(persistenceUnitProperties);
	}
		
	/**
	 * The default schema used by JPA persistence in the current thread. <p>
	 * 
	 * For example, if you use 'COMPANYA' as default schema, and you OX component
	 * or EJB3 entity is mapping to a table named 'ISSUE' when OX and JPA engine
	 * try to execute SQL they will use 'COMPANYA.ISSUE' as table name.<br>
	 */
	public static String getDefaultSchema() {
		return (String) getPersistenceUnitProperties().get(getHibernateDefaultSchemaPropertyName()); 
	}
	
	/**
	 * Change the default schema used by JPA persistence in the current thread. <p>
	 * 
	 * For example, if you use 'COMPANYA' as default schema, and you OX component
	 * or EJB3 entity is mapping to a table named 'ISSUE' when OX and JPA engine
	 * try to execute SQL they will use 'COMPANYA.ISSUE' as table name.<br>
	 */
	public static void setDefaultSchema(String defaultSchema) {
		Map properties = new HashMap(getPersistenceUnitProperties());
		if (Is.emptyString(defaultSchema)) properties.remove(getHibernateDefaultSchemaPropertyName());
		else properties.put(getHibernateDefaultSchemaPropertyName(), defaultSchema);		
		setPersistenceUnitProperties(properties);
	}
	
	/**
	 * Reset the info associated to the current thread. <p>
	 * 
	 * After call this method XPersistence works as default,
	 * all previous call to {@link #setPersistenceUnit}, 
	 * {@link #setPersistenceUnitProperties} or {@link #setDefaultSchema} 
	 * are annulled. <p>
	 */
	public static void reset() {
		currentPersistenceUnitProperties.set(null);
	}
	
	/**
	 * Read the property 'hibernate.default_schema' from META-INF/persistence.xml.<p>
	 * 
	 * @return
	 */
	private static String obtainDefaultSchemaFromPersistenceXML() {
		try {
			return PersistenceXml.getPropetyValue(getPersistenceUnit(), "hibernate.default_schema");
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("default_schema_warning"));
			return null; 
		}
	}
			
}
