package org.openxava.tab.impl;

/**
 * Home interface for Enterprise Bean: EntityTab
 */
public interface EntityTabHome extends javax.ejb.EJBHome {
	
	/**
	 * Creates a default instance of Session Bean: EntityTab
	 */
	public EntityTabRemote create() throws javax.ejb.CreateException, java.rmi.RemoteException;
	
		
}
