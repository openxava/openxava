package org.openxava.model;

import org.openxava.jpa.*;
import org.openxava.model.meta.*;

/**
 * To access to some persistence services in an abstract way. <p>
 * 
 * We need this class to write some code in OpenXava that works in the same
 * way if we are using Hibernate or JPA (or other persistence technology).<p>
 * 
 * This class can be used in custom application code, although for this case
 * maybe it's better to use directly the JPA (or Hibernate) API.<p>
 * 
 * This class uses the underlayer persistence provider associated to
 * the corresponding entity.<p>
 * 
 * If the methods fail they throw <code>RuntimeException</code>, the specific 
 * <code>RuntimeException</code> depends on the implementation technology.<br> 
 * 
 * @author Javier Paniza
 */

public class PersistenceFacade {

	
	/**
	 * Refresh the state of the instance from the database, 
	 * overwriting changes made to the entity, if any.<p>
	 * If the object is null or it's not managed simply do nothing,
	 * but not fails. 
	 */
	public static void refreshIfManaged(Object object) {
		if (object == null) return;		
		MetaModel.getForPOJO(object).getMetaComponent().getPersistenceProvider().refreshIfManaged(object); 		
	}

	/**
	 * The default schema for the underlying persistence engine in the current thread. <p>
	 * 
	 * @since 8.0
	 */
	public static String getDefaultSchema() {
		// By now we use XPersistence (JPA) ad hoc, but we could implement
		// a policy pattern on demand.
		return XPersistence.getDefaultSchema();
	}

	/**
	 * Changes the default schema for the underlying persistence engine in the current thread. <p>
	 * 
	 * @since 8.0
	 */
	public static void setDefaultSchema(String defaultSchema) {
		XPersistence.setDefaultSchema(defaultSchema);
	}

}
