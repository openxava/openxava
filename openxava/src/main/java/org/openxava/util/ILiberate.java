package org.openxava.util;


/**
 * Cualquier cosa que atrape recurso y tenga que liberarlos. <p>
 *
 * It is a approximation to work with object that have
 * to trap and release resources (connections to remote object,
 * databases, etc). The resources are obtained on demmand
 * (<i>lazy initialization</i>) and when you wish to release
 * you can call to {@link liberate}.<br>
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
   * @exception SystemException  Any problem releasing resources
   */
  void liberate() throws SystemException;
  
}
