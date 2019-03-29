package org.openxava.tab.impl;

import java.rmi.*;

import javax.ejb.*;



import org.apache.commons.logging.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class EntityTabFactory {
	
	private static Log log = LogFactory.getLog(EntityTabFactory.class);
			
	public static EntityTab create(String componentName) throws CreateException, RemoteException {
		EntityTab tab = new EntityTab();
		tab.setComponentName(componentName);
		try {
			tab.init();
		}
		catch (InitException ex) {
			log.error(ex.getMessage(), ex);
			throw new CreateException(XavaResources.getString("tab_create_default_error", componentName));
		}		
		return tab; 
	}
	
	public static EntityTab create(String componentName, String tabName) throws CreateException, RemoteException {
		EntityTab tab = new EntityTab();		
		tab.setComponentName(componentName);
		tab.setTabName(tabName);
		try {
			tab.init();
		}
		catch (InitException ex) {
			log.error(ex.getMessage(), ex);
			throw new CreateException(XavaResources.getString("tab_create_error", tabName, componentName));
		}		
		return tab; 		
	}
	
	/**
	 * An EntityTab with on-demmand data reading.
	 */
	public static EntityTab create(MetaTab metaTab) throws CreateException, RemoteException, XavaException {
		return create(metaTab, -1);
	}

	/**
	 * An EntityTab that load all data at once.
	 */
	public static EntityTab createAllData(MetaTab metaTab) throws CreateException, RemoteException, XavaException {
		return create(metaTab, Integer.MAX_VALUE);
	}	
	
	private static EntityTab create(MetaTab metaTab, int chunkSize) throws CreateException, RemoteException, XavaException {
		EntityTab tab = new EntityTab();		
		tab.setComponentName(metaTab.getMetaModel().getMetaComponent().getName());
		tab.setMetaTab(metaTab);
		if (chunkSize > 0) tab.setChunkSize(chunkSize);		
		try {
			tab.init();
		}
		catch (InitException ex) {
			log.error(ex.getMessage(), ex);
			throw new CreateException(XavaResources.getString("tab_create_error", metaTab.getName(), tab.getComponentName()));
		}		
		return tab; 		
	}
	
}

