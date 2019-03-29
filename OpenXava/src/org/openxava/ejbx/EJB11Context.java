package org.openxava.ejbx;

import java.io.*;
import java.security.*;
import java.sql.*;
import java.util.*;

import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;


/**
 * Implementation of <code>IEJBContext</code> for a EJB 1.1 server. <p>
 *
 * Since EJB 1.1 standardizes enough things only it's needed one version,
 * and it is not required additional properties files.<br>
 * If you can use {@link #getConnection() } without argument, you need to define
 * in the bean a property named DATA_SOURCE that indicate the default data source.<p>
 *
 * In the DATA_SOURCE property you can put also the username and password by default,
 * thus  <tt>datasource;usuario;clave</tt>.<br>
 *
 * @author  Javier Paniza
 */

public class EJB11Context implements IEJBContextInit, Serializable {

	private static final long serialVersionUID = -3914100041651579657L;

	private static Log log = LogFactory.getLog(EJB11Context.class);
	
	// If changed, change heading doc, getConnection() doc and IEJBContext doc 
	private final static String DATASOURCE_DEFAULT_PROPERTY = "DATA_SOURCE";

	private final static String PRE_DS = "java:comp/env/";
	private final static String PRE_PRO = "java:comp/env/";

	private EJBContext ejbContext;
	private transient Context jndiContext;
	private String defaultDataSource;
	private String user;
	private String password;
	

	private void assertEJBContext() throws IllegalStateException {
		if (ejbContext == null) {
			throw new IllegalStateException(XavaResources.getString("ejb11context_invariant"));
		}
	}
	
	/**
	 * It sets defaultDataSource, and if the user is not settled it sets it. 
	 *
	 * It trusts in a property value in this form: <tt>datasource;username;password</tt>, 
	 * although the separator can be a comma or colon.<br> 
	 */
	private void setDefaultDataSource() {
		defaultDataSource = getProperty(DATASOURCE_DEFAULT_PROPERTY);
		if (user == null) {
			StringTokenizer st = new StringTokenizer(defaultDataSource, ";,:");
			if (st.hasMoreTokens())
				defaultDataSource = st.nextToken();
			if (st.hasMoreTokens())
				user = st.nextToken();
			if (st.hasMoreTokens())
				password = st.nextToken();
		}
	}

	
	public Principal getCallerPrincipal() {
		assertEJBContext();
		return ejbContext.getCallerPrincipal();
	}
	
	/**
	 * The connection is obtained from data source defined in DATA_SOURCE property of bean. <br>
	 * 
	 * Also you can specify with code the default data source calling
	 * to {#setDefaultDataSource}, this have priority over the defined in DATA_SOURCE.
	 */
	public Connection getConnection() throws SQLException {
		if (defaultDataSource == null) {
			setDefaultDataSource();
			if (Is.emptyString(defaultDataSource)) {
				throw new SQLException(XavaResources.getString("ejb_datasource_required"));
			}
		}
		return getConnection(defaultDataSource);
	}

	public Connection getConnection(String name) throws SQLException {
		try {
			DataSource ds = (DataSource) getJndiContext().lookup(PRE_DS + name);
			if (user == null) {
				return ds.getConnection();
			} else {
				return ds.getConnection(user, password);
			}
		} catch (NamingException ex) {
			throw new SQLException(XavaResources.getString("datasource_not_found", name));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new SQLException(XavaResources.getString("datasource_not_found", name));									
		}

	}
	private Context getJndiContext() {
		if (jndiContext == null) {
			try {
				jndiContext = new InitialContext();
			} catch (NamingException ex) {
				throw new EJBException(XavaResources.getString("create_error", "InitialContext"));
			}
		}
		return jndiContext;
	}
 
	public String getProperty(String name) {
		Object rs = null;
		try {
			rs = getJndiContext().lookup(PRE_PRO + name);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			// returning null
		}
		if (rs == null)
			return null;
		return rs.toString();
	}
	

	public boolean isCallerInRole(String roleName) {
		assertEJBContext();
		return ejbContext.isCallerInRole(roleName);
	}
	
	public void setPassword(java.lang.String password) {
		this.password = password;
	}

	public void setDefaultDataSource(String dataSourceName) {
		defaultDataSource = dataSourceName;
	}

	public void setEJBContext(EJBContext ejbContext) {
		this.ejbContext = ejbContext;
	}
	
	public void setUser(java.lang.String user) {
		this.user = user;
	}
	
}