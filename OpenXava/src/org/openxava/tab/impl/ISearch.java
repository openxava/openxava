package org.openxava.tab.impl;

import java.rmi.*;

import javax.ejb.*;

/**
 * Allows search specifying a concrete condition. <p>
 * 
 * It uses EJB exceptions to facilitate a remote implementation.<br>
 * 
 * @author  Javier Paniza
 */

public interface ISearch {

  /**
   * Execute search. <p>
   * 
   * If there are no object then generate a empty result, but does not
   * throw a exception. <br>
   * 
   * @param condition  Condition to use in search.
   * @param key  Key to send to search 
   * @exception FinderException  Any logic problem on search
   * @exception RemoteException  Any system problem on search
   */  
  void search(String condition, Object key) throws FinderException, RemoteException;
  
}
