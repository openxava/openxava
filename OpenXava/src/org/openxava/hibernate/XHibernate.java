package org.openxava.hibernate;

import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.event.service.spi.*;
import org.hibernate.event.spi.*;
import org.hibernate.internal.*;
import org.openxava.hibernate.impl.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Allows to work easily with hibernate inside OpenXava applications. <p>
 * 
 * You can use this class in any part of your OpenXava application:
 * calculators, validators, actions, junit tests, etc.<br>
 * To use hibernate with this class you can write something as:
 * <pre>
 * Invoice invoice = new Invoice();
 * XHibernate.getSession().save(invoice);
 * </pre>
 * 
 * And no more.<br>
 * The method getSession() create a session and transaction the first time 
 * in the thread, and the OpenXava close the session at the end of action execution.<br>
 * Also the next code is legal:
 * <pre>
 * Invoice invoice = new Invoice();
 * XHibernate.getSession().save(invoice);
 * XHibernate.commit(); // In this way you commit and close manually.
 * ...
 * XHibernate.getSession().save(customer); // As session has been closed, a new one is created
 * </pre> 
 * 
 * <b>Per thread configuration</b><br>
 * XHibernate allows you to change the configuration in runtime affecting only
 * to the current thread of execution.<br>
 * For example, if you want to have two configuration: 'hibernate1.cfg.xml' and 
 * 'hibernate2.cfg.xml', and you want to change from one to other, it's easy:
 * <pre>
 * XHibernate.setConfigurationFile("/hibernate1.cfg.xml");
 * XHibernate.getSession().save(invoice); // Using hibernate1
 * ...
 * XHibernate.commit();
 * XHibernate.setConfigurationFile("/hibernate2.cfg.xml");
 * XHibernate.getSession().save(invoice); // Using hibernate2
 * ...
 * XHibernate.commit();
 * </pre>
 * And this change only affect to the current execution thread.<br>
 * Or if you want to change the default schema you can write:
 * <pre>
 * XHibernate.setDefaultSchema("COMPANY1");
 * XHibernate.getSession().save(invoice); // Save in INVOICE table of COMPANY1 Schema
 * ...
 * XHibernate.commit();
 * XHibernate.setDefaultSchema("COMPANY2");
 * XHibernate.getSession().save(invoice); // Save in INVOICE table of COMPANY2 Schema
 * ...
 * XHibernate.commit();  
 * </pre>
 * Also this only affect to the current thread.<br>
 * 
 * The method {@link #setSessionFactoryProperties} can be use in the same way
 * that {@link #setConfigurationFile} and {@link #setDefaultSchema}. <br>    
 * 
 * @author Javier Paniza
 */

public class XHibernate {

	private final static String DEFAULT_CFG_FILE = "/hibernate.cfg.xml";
	private final static String XAVA_CFG_FILE_KEY = "xava.configurationFile";
	private static Log log = LogFactory.getLog(XHibernate.class);
	final private static ThreadLocal currentSession = new ThreadLocal();	
	final private static ThreadLocal currentTransaction = new ThreadLocal();
	final private static ThreadLocal currentCmt = new ThreadLocal();	
	final private static ThreadLocal currentSessionFactoryProperties = new ThreadLocal();
	private static Map sessionFactories = new HashMap();
	private static Properties defaultSessionFactoryProperties;
	private static String hibernateDefaultSchemaPropertyName; 
	

	/**
	 * Session associated to current thread. <p>
	 * 
	 * If no session exists or it's closed, then create a new one, and start a transaction.<br>
	 *
	 * @return Not null
	 */
	public static Session getSession() {		
		Session s = (Session) currentSession.get();
		if (s == null || !s.isOpen()) {
			s = openSession();
		}		
		return s;
	}
	
	/**
	 * Create a new session. <p>
	 * 
	 * This session is not associated with the current thread,
	 * and no transaction is started.
	 *
	 * @return Not null
	 */
	public static Session createSession() {
		return getSessionFactory().openSession();
	}
				
	private static Session openSession() {
		Session s = getSessionFactory().openSession();
		currentTransaction.set(isCmt()?null:s.beginTransaction());
		currentSession.set(s);
		return s;
	}
			
	/**
	 * Commits changes and closes the session associated to current thread. <p>
	 * 
	 * If no session or the it is closed this method does nothing.<br>
	 * In most cases this method is called by OpenXava automatically, 
	 * hence the programmer that uses hibernate APIs does not need to call it.
	 */
	public static void commit() {		
		Session s = (Session) currentSession.get();
		if (s == null) return;
		if (s.isOpen()) {			
			Transaction t = (Transaction) currentTransaction.get();
			try {
				if (t != null && t.isActive()) t.commit();
				else s.flush();
			}
			finally {
				currentTransaction.set(null);
				currentSession.set(null);												
				s.close();
			}
		}
		else {			
			currentTransaction.set(null);
			currentSession.set(null);
		}
	}
	
	/**
	 * Rollback changes and closes the session associated to current thread. <p>
	 * 
	 * If no session or the it is closed this method does nothing.<br>
	 */
	public static void rollback() {
		Session s = (Session) currentSession.get();
		if (s == null) return;
		if (s.isOpen()) {
			Transaction t = (Transaction) currentTransaction.get();
			try {
				if (t != null) t.rollback();
			}
			finally {				
				currentTransaction.set(null);
				currentSession.set(null);
				s.close();
			}			
		}
		else {		
			currentTransaction.set(null);
			currentSession.set(null);
		}
	}	
	
	private static Configuration createConfiguration(String hibernateCfg) {
		try { 
			return new Configuration().configure(hibernateCfg); 
		}
		catch (NoSuchFieldError ex) {
			log.error(XavaResources.getString("incorrect_openxava_upgrade")); 
			throw ex;
		}
	}
	
	private static SessionFactory createSessionFactory(String hibernateCfg, Properties properties) throws HibernateException {
		try {
			Configuration configuration = createConfiguration(hibernateCfg).addProperties(properties);					
				
			for (Iterator it = MetaModel.getAllPojoGenerated().iterator(); it.hasNext();) {
				MetaModel model = (MetaModel) it.next();
				if (model.getMetaComponent().isTransient()) continue;
				try {
					configuration.addResource(model.getName() + ".hbm.xml");
				}
				catch (Exception ex) {
					log.error(XavaResources.getString("hibernate_mapping_not_loaded_warning", model.getName()), ex); 
				}
			}
						
			SessionFactory sessionFactory = configuration.buildSessionFactory(); 
			
			_registerEvents(sessionFactory);
	        return sessionFactory;        
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new HibernateException(XavaResources.getString("hibernate_session_factory_creation_error"));
		}
	}
	
	/**
	 * For implementation purpose. Don't use.
	 */
	public static void _registerEvents(SessionFactory sessionFactory) {
        EventListenerRegistry registry = 
        	((SessionFactoryImpl) sessionFactory)
        		.getServiceRegistry().getService(EventListenerRegistry.class);

		if (ReferenceMappingDetail.someMappingUsesConverters()) {				
			// toJava conversion is not enabled because in references it's useless thus we avoid an unnecessary overload
			ReferenceConverterToDBListener referenceConverterToDBListener = new ReferenceConverterToDBListener();
			registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(referenceConverterToDBListener);
			registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(referenceConverterToDBListener);				
		}
		
		if (MetaModel.someModelHasDefaultCalculatorOnCreate()) {
			registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(new DefaultValueCalculatorsListener());
		}
		
		if (MetaModel.someModelHasPostCreateCalculator()) {
			registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(CalculatorsListener.getInstance());
		}
		
		if (MetaModel.someModelHasPostModifyCalculator()) {				
			registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(CalculatorsListener.getInstance());
		} 
		
		if (MetaModel.someModelHasPreRemoveCalculator()) {				
			registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(CalculatorsListener.getInstance());
		}
		
		if (MetaModel.someModelHasPostLoadCalculator()) {				
			registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(CalculatorsListener.getInstance());
		}	        
	}


	private static SessionFactory getSessionFactory() throws HibernateException {
		Properties properties = getSessionFactoryProperties();
		SessionFactory sessionFactory = (SessionFactory) 
			sessionFactories.get(properties); 
		if (sessionFactory == null) {
			sessionFactory = createSessionFactory(getConfigurationFile(), properties);
			sessionFactories.put(new HashMap(properties), sessionFactory);
		}
		return sessionFactory;		
	}

	/**
	 * By default is <code>/hibernate.cfg.xml</code>. <p>
	 * 
	 * You must set value to this property before use any other method of this class.
	 * The value of this properties may be different for each thread.<br> 
	 */
	public static String getConfigurationFile() {
		return getSessionFactoryProperties().getProperty(XAVA_CFG_FILE_KEY);
	}

	/**
	 * By default is <code>/hibernate.cfg.xml</code>. <p>
	 * 
	 * You must set value to this property before use any other method of this class.
	 * If you change this value it only take effect for the current thread.<p>
	 * If you sent a <code>null</null> then <code>/hibernate.cfg.xml</code> is assumed.  
	 */	
	public static void setConfigurationFile(String configurationFile) {
		if (Is.emptyString(configurationFile)) configurationFile = DEFAULT_CFG_FILE;
		Properties properties = new Properties();
		properties.put(XAVA_CFG_FILE_KEY, configurationFile);		
		currentSessionFactoryProperties.set(properties);
		properties.putAll(getSessionFactoryProperties());
	}
	
	/**
	 * Indicate that the current thread is executing inside a CMT context. <p>
	 * 
	 * CMT is Container Managed Transaction. The usual inside EJB.
	 */
	public static void setCmt(boolean cmt) {
		currentCmt.set(cmt?"":null);
	}
	/**
	 * Indicate that the current thread is executing inside a CMT context. <p>
	 * 
	 * CMT is Container Managed Transaction. The usual inside EJB.
	 */	
	public static boolean isCmt() { 
		return currentCmt.get() != null;
	}
	
	/**
	 * The properties sent to Hibernate to in order to create the SessionFactory. <p>
	 * 
	 * The value can be different for each execution thread.<br>
	 *  
	 * @return Not null
	 */
	public static Properties getSessionFactoryProperties() { 
		Properties result = (Properties) currentSessionFactoryProperties.get();
		if (result == null) return getDefaultSessionFactoryProperties();
		return result;
	}
	
	private static Properties getDefaultSessionFactoryProperties() {
		if (defaultSessionFactoryProperties == null) {
			defaultSessionFactoryProperties = new Properties();
			defaultSessionFactoryProperties.put(XAVA_CFG_FILE_KEY, DEFAULT_CFG_FILE);
			
			String defaultSchema = obtainDefaultSchema();
			if (!Is.emptyString(defaultSchema)) {
				defaultSessionFactoryProperties.put(getHibernateDefaultSchemaPropertyName(), defaultSchema); 
			}
		}
		return defaultSessionFactoryProperties;
	}

	private static String obtainDefaultSchema() {		
		return createConfiguration(DEFAULT_CFG_FILE).getProperty("hibernate.default_schema"); 
	}

	/**
	 * Set the properties to send to Hibernate in order to create the SessionFactory. <p>
	 * 
	 * This only apply to the current execution thread.<br>
	 *  
	 * @return Not null
	 */
	public static void setSessionFactoryProperties(Properties sessionFactoryProperties) { 
		if (sessionFactoryProperties == null) sessionFactoryProperties = new Properties();
		sessionFactoryProperties.put(XAVA_CFG_FILE_KEY, getConfigurationFile());
		currentSessionFactoryProperties.set(sessionFactoryProperties);
	}
		
	/**
	 * The default schema used by Hibernate in the current thread. <p>
	 * 
	 * For example, if you use 'COMPANYA' as default schema, and you OX component
	 * is mapping to a table named 'ISSUE' when OX and JPA engine
	 * try to execute SQL they will use 'COMPANYA.ISSUE' as table name.<br>
	 */
	public static String getDefaultSchema() {
		return getSessionFactoryProperties().getProperty(getHibernateDefaultSchemaPropertyName()); 
	}
	
	/**
	 * Change the default schema used by Hibernate in the current thread. <p>
	 * 
	 * For example, if you use 'COMPANYA' as default schema, and you OX component
	 * or EJB3 entity is mapping to a table named 'ISSUE' when OX and JPA engine
	 * try to execute SQL they will use 'COMPANYA.ISSUE' as table name.<br>
	 */
	public static void setDefaultSchema(String defaultSchema) {
		Properties properties = new Properties(getSessionFactoryProperties());
		if (Is.emptyString(defaultSchema)) properties.remove(getHibernateDefaultSchemaPropertyName());
		else properties.put(getHibernateDefaultSchemaPropertyName(), defaultSchema);
		setSessionFactoryProperties(properties);		
	}
	
	private static String getHibernateDefaultSchemaPropertyName() {
		if (hibernateDefaultSchemaPropertyName == null) {
			// Not get from getSessionFactory() in order to PDF generation works when Hibernate connection does not work
			Configuration cfg = createConfiguration(getConfigurationFile());
			String dialect = cfg.getProperty("hibernate.dialect");
			// Saving in hibernateDefaultSchemaPropertyName only works when all the hibernate.cfg.xml files goes against the same type of database
			// For supporting different database vendor for each configuration we should turn hibernateDefaultSchemaPropertyName into a Map.  
			if (dialect != null && dialect.toLowerCase().contains("mysql")) hibernateDefaultSchemaPropertyName = "hibernate.default_catalog";
			else if (!Is.emptyString(cfg.getProperty("hibernate.default_catalog"))) hibernateDefaultSchemaPropertyName = "hibernate.default_catalog";
			else hibernateDefaultSchemaPropertyName = "hibernate.default_schema";			
		}
		return hibernateDefaultSchemaPropertyName;
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
		currentSessionFactoryProperties.set(null);
	}
	
}
