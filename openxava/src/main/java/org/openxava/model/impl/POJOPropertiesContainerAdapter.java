package org.openxava.model.impl;

import java.lang.reflect.*;
import java.rmi.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

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
		// tmr ini
		catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace(); // tmr
			System.out.println("[POJOPropertiesContainerAdapter.executeGets] ex.getCause().getMessage()=" + ex.getCause().getMessage()); // tmr
			System.out.println("[POJOPropertiesContainerAdapter.executeGets] ex.getCause().getLocalizedMessage()=" + ex.getCause().getLocalizedMessage()); // tmr
			throw new PropertiesContainerException("get_properties_error", ex.getCause().getLocalizedMessage());
		}
		// tmr fin
		catch (Exception ex) {
			System.out.println("[POJOPropertiesContainerAdapter.executeGets] ex.getMessage()=" + ex.getMessage()); // tmr
			System.out.println("[POJOPropertiesContainerAdapter.executeGets] ex.getLocalizedMessage()=" + ex.getLocalizedMessage()); // tmr
			ex.printStackTrace(); // tmr
			throw new PropertiesContainerException("get_properties_error", ex.getLocalizedMessage());
		}
	}

	public void executeSets(Map properties) throws javax.validation.ValidationException, RemoteException {
		try {
			propertiesManager.executeSets(properties);	
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof org.openxava.validators.ValidationException) {
				throw (org.openxava.validators.ValidationException) ex.getTargetException(); 
			}
			if (ex.getTargetException() instanceof javax.validation.ValidationException) {
				throw (javax.validation.ValidationException) ex.getTargetException(); 
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
