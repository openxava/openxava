package org.openxava.ejbx;

import javax.naming.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import java.util.*;

/**
 * Factory for <code>IContextProvider</code> in order to accede to EJBs. 
 *
 * This class provide the implementation of {@link BeansContext}, thus
 * it's possible to setup the its behaviour declaratorily.
 * It's requiredt to have a properties file called  
 * <code>BeansContext.properties</code> within CLASSPATH. This file
 * must to have the form <code>name=class</code>.
 * Like this:
 * <pre>
 * _defecto=javacomp
 * _subcontexto=
 * javacomp=puntocom.negocio.context.JavaCompBeansContextProvider
 * jndi=puntocom.negocio.context.JndiBeansContextProvider
 * </pre>
 * Or in English:
 * <pre>
 * _default=javacomp
 * _subcontext=
 * javacomp=puntocom.negocio.context.JavaCompBeansContextProvider
 * jndi=puntocom.negocio.context.JndiBeansContextProvider
 * </pre> 
 * The entry <code>_default/_defecto</code> indicates the object used
 * by default. The default object is created calling to  
 * indica el objeto usado por defecto. El {@link #create()} without arguments.
 * The classes used have to implement {@link IContextProvider}.<br>
 * The entry <code>_subcontext/_subcontexto</code> indicates the lookup subcontext used,
 * It can be left in blank or omit the entry, then it lookup from root subcontext.<br>
 *
 * @author  Javier Paniza
 */

public class BeansContextProviderFactory {
	
  private static Log log = LogFactory.getLog(BeansContextProviderFactory.class);	

  // If final variable are changed change the doc of heading and create() too. 	
  private final static String PROPERTIES_FILE = "BeansContext.properties";
  private final static String SUBCONTEXT_PROPERTY_ES = "_subcontexto";
  private final static String SUBCONTEXT_PROPERTY_EN = "_subcontext";
  
  private static boolean subcontextReaded = false;
  private static String subcontext;  
  
  private static Factory impl = new Factory( // In all CLASSPATH
	BeansContextProviderFactory.class.getClassLoader().getResource(PROPERTIES_FILE));
//  private static Factory impl = new Factory( // Where is the class
//    BeansContextProviderFactory.class.getResource(PROPERTIES_FILE));


  
  
  /**
   * Create a context provider to lookup EJBs by default. <p>
   *
   * <b>Postcondition:</b>
   * <ul>
   * <li> return != null
   * </ul>
   * It works as indicate in entry <code>_default/_defecto</code> of 
   * <code>BeansContext.properties</code>.<br>
   * @exception InitException  Some problem on init.
   */
  public static IContextProvider create() throws InitException {
  	return (IContextProvider) impl.create();
  }
  
  /**
   * Create a context provider to lookup EJBs. <p>
   *
   * <b>Postcondition:</b>
   * <ul>
   * <li> return != null
   * </ul>
   * The argument is a identifier name of context provider, whose it's registered
   * in <code>BeansContext.properties</code> file.<br>
   * @exception InitException  Some problem on init.
   */
  public static IContextProvider create(String name) throws InitException {
  	return (IContextProvider) impl.create(name);
  }
  
	/**
	 * @return Can be null.
	 */
	static String getSubcontext() throws NamingException {
		if (!subcontextReaded) { 
			try {
				Properties pro = new Properties();			
				pro.load(BeansContextProviderFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
				subcontext = pro.getProperty(SUBCONTEXT_PROPERTY_EN);
				if (Is.emptyString(subcontext)) subcontext = pro.getProperty(SUBCONTEXT_PROPERTY_ES);
				if (Is.emptyString(subcontext)) subcontext = null;
				subcontextReaded = true;
				log.debug("subcontext=" + subcontext);
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new NamingException(XavaResources.getString("subcontext_error", ex.getLocalizedMessage()));
			}
		}
		return subcontext;
	}
	
}
