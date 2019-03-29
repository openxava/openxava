package org.openxava.tab.impl;

import java.rmi.*;

import javax.ejb.*;

/**
 * Interface to facilite a remote <b>impl</b>ementation of
 * a {@link EntityTab}. <p>
 *
 * @author  Javier Paniza
 */

public interface IEntityTabImpl extends IWithXTableModel, ISearch, IDataReader {

  /**
   * Search a concrete entity from a key. <p>
   * 
   * Ususally thie key is obtained from columns of table (IXTableModel).<br>
   * This method is used from IXTableModel.getObjectAt. It is not normal
   * that a application programmer call this method directly.<br>
   */
  Object findEntity(Object [] clave) throws FinderException, RemoteException;

  Number getSum(String property) throws RemoteException; 
  
  /** @since 5.7 */
  int getChunkSize(); 
  
}
