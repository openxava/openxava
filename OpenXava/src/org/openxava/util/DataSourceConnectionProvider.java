package org.openxava.util;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.sql.*;
import javax.xml.parsers.*;

import org.apache.commons.logging.*;
import org.hibernate.internal.*;
import org.openxava.component.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.jpa.impl.*;
import org.w3c.dom.*;

/**
 * Adapter from JNDI DataSource interface to IConnectionProvider interface.
 * 
 * @author Javier Paniza
 */
public class DataSourceConnectionProvider implements IConnectionProvider, Serializable {
	
	private static final long serialVersionUID = 5262771696899604060L;
	
	private final static String DEFAULT_JPA_PERSISTENCE_UNIT="__DEFAULT__"; 
	private static Log log = LogFactory.getLog(DataSourceConnectionProvider.class);
		
	private static Properties datasourcesJNDIByPackage;
	private static Map providers;
	private static boolean useHibernateConnection = false;
	private static Map jpaDataSources;
	private static IConnectionRefiner connectionRefiner; 
			
	private String dataSourceJNDI;	
	private String user;
	private String password;
		
	public static IConnectionProvider createByComponent(String componentName) throws XavaException {
		MetaComponent component =MetaComponent.get(componentName); 				
		String jndi = null;		
		if (component.getMetaEntity().isXmlComponent()) { 
			String packageName = component.getPackageNameWithSlashWithoutModel();
			jndi = getDatasourcesJNDIByPackage().getProperty(packageName);			
			if (Is.emptyString(jndi)) {
				throw new XavaException("no_data_source_for_component", componentName); 
			}
		}
		DataSourceConnectionProvider provider = new DataSourceConnectionProvider();		
		provider.setDataSourceJNDI(jndi);
		return provider;
	}
		
	/**
	 * Extract the JNDI of data source from JPA persistence.xml file.
	 * 
	 * It can be a empty string because it's possible to have a jpa unit without datasource
	 * for example with direct access to data with JDBC, or so.
	 */
	private static String getJPADataSource() { 
		if (jpaDataSources == null) {
			loadJPADataSources();
		}
		String result = (String) jpaDataSources.get(XPersistence.getPersistenceUnit());
		if (result != null) return result;
		result = (String) jpaDataSources.get(DEFAULT_JPA_PERSISTENCE_UNIT);
		if (Is.emptyString(result) && !isUseHibernateConnection()) {  
			throw new XavaException("no_jpa_data_source_for_entity");  
		}
		return result;
	}
	
	private static String getDataSourceFromElement(Element element) { 
		String dataSource = getNodeValue(element, "non-jta-data-source");
		if (!Is.emptyString(dataSource)) return dataSource;
		return getNodeValue(element, "jta-data-source");
	}

	private static String getNodeValue(Element element, String tagName) {
		NodeList nodes = element.getElementsByTagName(tagName);
		int length = nodes.getLength();
		for (int i=0; i < length; i++) {
			String datasource = nodes.item(i).getFirstChild().getNodeValue();
			if (datasource != null) { 
				return datasource;
			}
		}
		return "";
	}	

	private static void loadJPADataSources() {
		jpaDataSources = new HashMap();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL url = PersistenceXml.getResource(); 
			Document doc = builder.parse(url.toExternalForm());
			NodeList units = doc.getElementsByTagName("persistence-unit");
			int unitsCount = units.getLength();
			for (int iUnits=0; iUnits<unitsCount; iUnits++) {
				Element unit = (Element) units.item(iUnits);
				String unitName = unit.getAttribute("name");
				String dataSource = getDataSourceFromElement(unit);
				if (jpaDataSources.isEmpty()) { // first time
					jpaDataSources.put(DEFAULT_JPA_PERSISTENCE_UNIT, dataSource); // The first is the default one
				}
				jpaDataSources.put(unitName, dataSource);
			}			
		}
		catch (Exception ex) {
			jpaDataSources = null;
			log.error(ex.getMessage(), ex); 
		}		
	}
	


	public static IConnectionProvider getByComponent(String componentName) throws XavaException {
		if (providers == null) providers = new HashMap();
		componentName = Strings.firstToken(componentName, "."); 
		IConnectionProvider provider = (IConnectionProvider) providers.get(componentName);
		if (provider == null) {
			provider = createByComponent(componentName);
			providers.put(componentName, provider);
		}		
		return provider;
	}
	
	/**
	 * DataSource to wrap
	 * @throws NamingException
	 */	
	public DataSource getDataSource() throws NamingException {
		Context ctx = new InitialContext();
		return (DataSource) ctx.lookup(dataSourceJNDI==null?getJPADataSource():dataSourceJNDI);
	}
		
	/**
	 * JNDI of DataSource to wrap. <p>
	 * Only works if datasource == null 
	 */
	public String getDataSourceJNDI() {
		return dataSourceJNDI;
	}

	/**
	 * JNDI of DataSource to wrap. <p>
	 * Only works if datasource == null 
	 */	
	public void setDataSourceJNDI(String dataSourceJDNI) {
		this.dataSourceJNDI = dataSourceJDNI;
	}
	
	public Connection getConnection() throws SQLException {
		if (isUseHibernateConnection()) {		
			SessionImpl session = ((SessionImpl) XHibernate.createSession());
			Connection con = ((SessionImpl) XHibernate.createSession()).connection(); 
			session.close();
			return con;
		}			
		try {
			Connection con = null;
			if (Is.emptyString(getUser())) {
				con = getDataSource().getConnection();
			}
			else {
				con = getDataSource().getConnection(getUser(), getPassword());
			}		
			refine(con);
			return con;
		}
		catch (SQLException ex) {
			throw ex;			
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("get_connection_error"), ex); 
			throw new SQLException(ex.getLocalizedMessage());			
		}			
	}
	
	private void refine(Connection con) throws Exception { 
		IConnectionRefiner refiner = getConnectionRefiner();
		if (refiner != null) refiner.refine(con);		
	}

	private static IConnectionRefiner getConnectionRefiner() throws Exception { 
		if (connectionRefiner == null) {
			String refinerClass = XavaPreferences.getInstance().getConnectionRefinerClass();
			if (Is.emptyString(refinerClass)) return null;
			connectionRefiner = (IConnectionRefiner) Class.forName(refinerClass).newInstance();
		}
		return connectionRefiner;
	}

	public Connection getConnection(String dataSourceName) throws SQLException {		
		return getConnection();
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public String getUser() {
		return user;
	}
	
	public void setDefaultDataSource(String dataSourceName) {
	}
		
	private static Properties getDatasourcesJNDIByPackage() throws XavaException {
		if (datasourcesJNDIByPackage == null) {
			try {
				PropertiesReader reader = new PropertiesReader(DataSourceConnectionProvider.class, "datasource.properties"); 				
				datasourcesJNDIByPackage = reader.get();				
			}
			catch (IOException ex) {
				log.error(ex.getMessage(), ex);
				throw new XavaException(ex.getLocalizedMessage());
			}
		}		
		return datasourcesJNDIByPackage;
	}

	/**
	 * If <code>true</code> then all intances use hibernate connection for obtain 
	 * connection, instead of data source connection pool. <p>
	 * 
	 * Useful for using outside an application server, for example, in a
	 * junit test. 
	 */	
	public static boolean isUseHibernateConnection() {
		return useHibernateConnection;
	}

	/**
	 * If <code>true</code> then all intances use hibernate connection for obtain 
	 * connection, instead of data source connection pool. <p>
	 * 
	 * Useful for using outside an application server, for example, in a
	 * junit test. 
	 */
	public static void setUseHibernateConnection(boolean useHibernateConnection) {
		DataSourceConnectionProvider.useHibernateConnection = useHibernateConnection;
	}
	
}
