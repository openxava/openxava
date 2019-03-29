package org.openxava.ejbx;

import java.io.*;
import java.security.*;
import java.sql.*;
import java.util.*;

import javax.ejb.*;
import javax.naming.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;


/**
 * Base class for a EJB. <p>

 * Of course, the use of this class is optional, but
 * it provides a lot of advantages.<br>
 * 
 * Basically it have methods that wrap the access to
 * <coce>IEJBContext</code>, creating it and wrap the
 * exceptions in <code>EJBException</code> format.<br>  
 *
 * <b>Important:</b> It's required call to {@link #setEJBContext}
 * with a not null value before use any method of this class.<br> 
 * 
 * @author  Javier Paniza
 */

public class EJBBase implements Serializable {
	
  private static final long serialVersionUID = 3388107305521009306L;

  private static Log log = LogFactory.getLog(EJBBase.class);
  
  static {
	  XSystem._setOnServer();
  }

  private EJBContext ejbContext; // Of EJB
  private IEJBContext portableContext; // Of OpenXava (portable)
  private transient IContext context; // For look up other EJBs
  private Map booleanVars;
  

  public EJBBase() {
  }
  
  /**
   * As <code>EJBContext.getCallerPrincipal</code> in EJB 1.1. <br>
   */
  public Principal getCallerPrincipal() {
  	return getPortableContext().getCallerPrincipal();
  }
  
  /**
   * Default JDBC connection. <br>
   */
  public Connection getConnection() {
  	try {
  		return getPortableContext().getConnection();
  	}
  	catch (SQLException ex) {
  		throw new EJBException(ex.getLocalizedMessage());
  	}
  }
  
  /**
   * A JDBC connection from identifier name. <br>
   *
   * @param dataSourceName  Name of data source from obtain the connection.
   */
  public Connection getConnection(String dataSourceName) {
  	try {
  		return getPortableContext().getConnection(dataSourceName);
  	}
  	catch (SQLException ex) {
  		throw new EJBException(ex.getLocalizedMessage());
  	}
  }
  
  /**
   * Context used for look up <i>homes</i> of EJBs. <p>
   *
   * The look up is direct without using standards subcontext like
   * <tt>java:comp/env/ejb</tt>. Of course, you can use subcontext for
   * qualifying way.<br>  
   */
  public IContext getContext() {
  	if (context == null) {
  		try {
  			context = BeansContext.get();
  		}
  		catch (NamingException ex) {
  			log.error(ex.getMessage(), ex);
  			throw new EJBException(ex.getMessage());
  		}
  	}
  	return context;
  }
  
  /**
   * The context used for implementing the methods. <br>
   */
  protected IEJBContext getPortableContext() {
  	if (portableContext == null) {
  		if (ejbContext == null)
  			throw new IllegalStateException(XavaResources.getString("ejbcontext_precondition"));
  		try {
  			portableContext = EJBContextFactory.create(ejbContext);
  		}
  		catch (Exception ex) {
  			log.error(ex.getMessage(), ex);
  			throw new EJBException(XavaResources.getString("create_context_error"));
  		}
  	}
  	return portableContext;
  }
  
  /**
   * Property value from name. <br>
   *
   * Return <code>null</code> if the property does not
   * exist or it can not obtain its value for another problem.<br>
   */
  public String getProperty(String nombre)  {
  	return getPortableContext().getProperty(nombre);
  }
  
  
	private Map getBooleanVars() {
		if (booleanVars == null) {
			booleanVars = new HashMap();
		}
		return booleanVars;
	}
	
  /**
   * As <code>EJBContext.isCallerInRole</code> in EJB 1.1. <br>
   */
  public boolean isCallerInRole(String roleName) {
  	return getPortableContext().isCallerInRole(roleName);
  }
  
	/**
	 * Examine a environment variable that can contain true or false
	 * (ignoring case). <p>
	 *
	 * @param variable  Name of environment variable to examine.
	 * @param bean  Name of current bean, used in error messages.
	 * @return boolean <tt>true</tt> o <tt>false</tt>.
	 * @exception EJBException  If the variable is not defined.
	 */
	protected boolean isTrue(String variable, String bean) {
		Boolean result = (Boolean) getBooleanVars().get(variable);
		if (result == null) {
			String value = getProperty(variable);
			if (value == null) {
				throw new EJBException(XavaResources.getString("var_in_ejb_required", variable, bean));
			}
			if (value.trim().equalsIgnoreCase("true")) {
				result = Boolean.TRUE;
			}
			else if (value.trim().equalsIgnoreCase("false")) {
				result = Boolean.FALSE;
			}
			else {
				throw new EJBException(XavaResources.getString("var_in_ejb_invalid_boolean_value", value, variable, bean));			
			}
			getBooleanVars().put(variable, result);
		}
		return result.booleanValue();
	}
	
  /**
   * Sets the name of datasource used when call to {@link #getConnection()}. <br> 
   */
  public void setDefaultDataSource(String nombreDataSource) {
  	getPortableContext().setDefaultDataSource(nombreDataSource);
  }
  
  /**
   * Sets the subcontext used to implement the methods. <br>
   *
   * It'r required call to this method sending not null before
   * use any other method of this class.<br>
   * You can send null, in this case the context is disabled, it's 
   * good call to <code>setEJBContext(null)</code> when the EJB Server
   * call to <code>unsetEntityContext()</code>. <br>
   */
  protected void setEJBContext(EJBContext ejbContext) {
  	this.ejbContext = ejbContext;
  	if (ejbContext == null) portableContext = null;
  }
  
}
