package org.openxava.tab.impl;

import org.openxava.util.*;

import org.openxava.model.*;

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
   * @throws SystemException
   */
  Object findEntity(Object [] clave) throws FinderException ;

  /**
  * @throws SystemException
   */
  Number getSum(String property) ; 
  
  /** @since 5.7 */
  int getChunkSize(); 
  
}
