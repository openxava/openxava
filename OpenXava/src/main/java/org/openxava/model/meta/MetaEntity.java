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
	 */
	public Collection getKeyFields() throws XavaException {
		if (keyFields == null) {
			keyFields = new ArrayList();	
			keyFields.addAll(getAllKeyPropertiesNames());
		}		
		return keyFields;
	}
		
	public boolean isKey(String propertyName) throws XavaException {		 	
		if ((isAnnotatedEJB3() || isPojoGenerated()) &&  super.isKey(propertyName)) return true;
		return getKeyFields().contains(propertyName);		
	}
	
	public Class getPropertiesClass() throws XavaException {
		if (isAnnotatedEJB3()) return getPOJOClass();
		return super.getPropertiesClass();
	}
	
	/**
	 * If has key fields that aren't properties hence does not math with key properties. <p>
	 */
	public boolean hasHiddenKeys() throws XavaException {		
		return !getKeyPropertiesNames().containsAll(getKeyFields());
	}
	
	public String getId() {
		return getName();
	}
	
	public ModelMapping getMapping() throws XavaException {
		return getMetaComponent().getEntityMapping();
	}
			
}