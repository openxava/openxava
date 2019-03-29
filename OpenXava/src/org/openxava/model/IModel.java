package org.openxava.model;

import java.rmi.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Interface to be implemented by all model classes. <p>
 * 
 * The model classes may be EntityBeans EJB 2 or POJOs (for JDO, EJB3 or Hibernate).
 * 
 * @author Javier Paniza
 */

public interface IModel {
	
	/**
	 * Returns metadata about object. <p>
	 * 
	 * @return  Not null.
	 * @exception XavaException  Any problem related to OpenXava.
	 * @exception RemoteException  System problem.
	 */
	MetaModel getMetaModel() throws XavaException, RemoteException;

}
