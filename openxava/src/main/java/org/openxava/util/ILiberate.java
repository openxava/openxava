package org.openxava.util;

import java.rmi.*;


/**
 * Cualquier cosa que atrape recurso y tenga que liberarlos. <p>
 *
 * It is a approximation to work with object that have
 * to trap and release resources (connections to remote object,
 * databases, etc). The resources are obtained on demmand
 * (<i>lazy initialization</i>) and when you wish to release
 * you can call to {@link liberate}.<br>
 * 
 * RemoteException is used to allow implement by a remote object,
 * although this is not mandatory.<br> 
 *
 * @author  Javier Paniza
 */

public interface ILiberate {

  /**
   * Liberate all catched resources. <p> 
   * 
   * It is important to call this method when you do not need any more this object,
   * although can be called in any momment of object life cycle. 
   * After you call this method the object continues being usable.<br>  
   *
   * @exception RemoteException  Any problem releasing resources
   */
  void liberate() throws RemoteException;
  
}
