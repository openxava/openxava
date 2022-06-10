package org.openxava.model.impl;

import java.util.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Persistence provider where modifications are done calling explicit to modify(). <p> 
 * 
 * Typically for for JDBC, Web Services, etc. NOT for ORMs such as Hibernate, JPA, etc.
 * where the modifications are implicit over the touched objects.
 * 
 * @since 5.6
 * @author Javier Paniza
 */

public interface IExplicitModifyPersistenceProvider extends IPersistenceProvider { 
	
	/**
	 * Modify the object with that key from the values. 
	 * 
	 * Only applies if isExplicitModify() is true.
	 * @since 5.6
	 */
	void modify(MetaModel metaModel, Map keyValues, Map values) throws ValidationException, XavaException; 

}
