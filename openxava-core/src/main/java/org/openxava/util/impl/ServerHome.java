package org.openxava.util.impl;

public interface ServerHome extends javax.ejb.EJBHome {

	ServerRemote create()
		throws javax.ejb.CreateException, java.rmi.RemoteException;
}
