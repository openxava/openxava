package org.openxava.model.meta;

import java.util.*;



import org.openxava.mapping.*;
import org.openxava.util.*;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class MetaEntity extends MetaModel {
	
	private Collection keyFields;

	/**
	 * @return The names of key fields. Of <tt>String</tt>.
	 * @throws XavaException
	 */
	public Collection getKeyFields() {
		if (keyFields == null) {
			keyFields = new ArrayList();	
			keyFields.addAll(getAllKeyPropertiesNames());
		}		
		return keyFields;
	}
		
	/**
	* @throws XavaException
	 */
	public boolean isKey(String propertyName) {		 	
		if ((isAnnotatedEJB3() || isPojoGenerated()) &&  super.isKey(propertyName)) return true;
		return getKeyFields().contains(propertyName);		
	}
	
	/**
	* @throws XavaException
	 */
	public Class getPropertiesClass() {
		if (isAnnotatedEJB3()) return getPOJOClass();
		return super.getPropertiesClass();
	}
	
	/**
	 * If has key fields that aren't properties hence does not math with key properties. <p>
	 * @throws XavaException
	 */
	public boolean hasHiddenKeys() {		
		return !getKeyPropertiesNames().containsAll(getKeyFields());
	}
	
	public String getId() {
		return getName();
	}
	
	/**
	* @throws XavaException
	 */
	public ModelMapping getMapping() {
		return getMetaComponent().getEntityMapping();
	}
			
}