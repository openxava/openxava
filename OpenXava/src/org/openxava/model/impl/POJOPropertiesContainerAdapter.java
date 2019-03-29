package org.openxava.model.impl;

import java.lang.reflect.*;
import java.rmi.*;
import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */
public class POJOPropertiesContainerAdapter implements IPropertiesContainer {
	
	private static Log log = LogFactory.getLog(POJOPropertiesContainerAdapter.class);
	private PropertiesManager propertiesManager;	
	
	public POJOPropertiesContainerAdapter(Object object) {
		propertiesManager = new PropertiesManager(object);
	}

	public Map executeGets(String properties) throws RemoteException {
		try {
			return propertiesManager.executeGets(properties);	
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("get_properties_error", ex.getLocalizedMessage()));
		}
	}

	public void executeSets(Map properties) throws ValidationException, RemoteException {
		try {
			propertiesManager.executeSets(properties);	
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof ValidationException) {
				throw (ValidationException) ex.getTargetException(); 
			}
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("set_properties_error", ex.getLocalizedMessage()));			
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("set_properties_error", ex.getLocalizedMessage()));
		}		
	}

}
