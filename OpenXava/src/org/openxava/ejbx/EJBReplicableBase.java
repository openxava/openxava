package org.openxava.ejbx;

import java.lang.reflect.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.validators.*;


/**
 * Implementation base class for a <i>EntityBean</i> that can be replicated. <p>
 *
 * A example of a <i>EntityBean</i> replicable:
 * <pre>
 * public class PersonBean extends EJBReplicableBase {
 *
 *   public String name;
 *   public int age;
 *
 *   public PersonaPK ejbCreate(Map properties) {
 *     executeSets(properties);
 *     return null;
 *   }
 *
 *   public void ejbPostCreate(Map properties) {
 *   }
 *
 *   public String getName() {
 *     return name;
 *   }
 *
 *   public void setName(String name) {
 *     this.name = name;
 *   }
 *
 *   public String getName() {
 *     return name;
 *   }
 *
 *   public void setName(String name) {
 *     this.name = name;
 *   }
 *
 * }
 * </pre>
 *
 * @author Javier Paniza
 */

public class EJBReplicableBase extends EntityBase implements IPropertiesContainer {

	private static Log log = LogFactory.getLog(EJBReplicableBase.class);
	private PropertiesManager propertiesManager = new PropertiesManager(this);

	public Map executeGets(String propertiesToReplicate) {
		try {
			return propertiesManager.executeGets(propertiesToReplicate);
		}
		catch (PropertiesManagerException ex) {
			throw new EJBException(ex.getMessage());
		}
		catch (InvocationTargetException ex) {
			log.error(ex.getMessage(), ex);
			throw new EJBException(XavaResources.getString("get_properties_error", ex.getTargetException().getMessage()));
		}
	}    

	public void executeSets(Map propertiesToUpdate) throws ValidationException {
		try {				
			propertiesManager.executeSets(propertiesToUpdate);
		}
		catch (PropertiesManagerException ex) {
			throw new EJBException(ex.getMessage());
		}
		catch (InvocationTargetException ex) {
			Throwable cause = ex.getTargetException();
			if (cause instanceof ValidationException) {
				throw (ValidationException) cause;
			}
			else {
				log.error(ex.getMessage(), ex);
				throw new EJBException(XavaResources.getString("set_properties_error", ex.getTargetException().getMessage()));
			}
		}	  	
	}    

}
