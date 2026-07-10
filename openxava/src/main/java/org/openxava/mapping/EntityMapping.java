package org.openxava.mapping;



import org.openxava.model.meta.*;
import org.openxava.util.*;


/** 
 * @author Javier Paniza
 */

public class EntityMapping extends ModelMapping {
	
	
	
	/**
	* @throws XavaException
	 */
	public String getModelName() {
		return getMetaModel().getName();
	}

	/**
	* @throws XavaException
	 */
	public MetaModel getMetaModel() {		
		return getMetaComponent().getMetaEntity();
	}
	
	

}


