package org.openxava.test.model;

import java.rmi.*;

/**
 * @author Javier Paniza
 */
public interface IWithName {
	
	String getName() throws RemoteException;

}
