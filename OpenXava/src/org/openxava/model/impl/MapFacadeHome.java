package org.openxava.model.impl;

public interface MapFacadeHome extends javax.ejb.EJBHome {

	org.openxava.model.impl.MapFacadeRemote create() throws javax.ejb.CreateException, java.rmi.RemoteException;
	
}
