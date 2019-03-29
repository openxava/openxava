package org.openxava.util.impl;

import java.rmi.*;

import org.openxava.actions.*;
import org.openxava.calculators.*;


public interface ServerRemote extends javax.ejb.EJBObject {
	
	Object calculate(ICalculator calculator) throws Exception, RemoteException;
	
	Object calculateWithoutTransaction(ICalculator calculator) throws Exception, RemoteException;	

	IRemoteAction execute(IRemoteAction action) throws Exception, RemoteException;
}