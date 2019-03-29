package org.openxava.test.model;	

import org.openxava.model.*;

public interface IFamily extends IModel {
	
	
	
  public java.lang.String getOid(  )
  	throws java.rmi.RemoteException;

  public int getNumber(  )
  	throws java.rmi.RemoteException;

  public void setNumber( int newNumber )
  	throws java.rmi.RemoteException;

  public java.lang.String getDescription(  )
  	throws java.rmi.RemoteException;

  public void setDescription( java.lang.String newDescription )
  	throws java.rmi.RemoteException;

}
