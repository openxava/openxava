package org.openxava.util.meta;

import java.io.*;
import java.util.*;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class MetaSetsContainer implements Serializable {
		
	private Collection metaSets;
	
	
		
	public void addMetaSet(MetaSet metaSet) {
		if (metaSets == null) {
			metaSets = new ArrayList();
		}
		metaSets.add(metaSet);		
	}
	
	protected void assignPropertiesValues(Object object) throws Exception {		
		PropertiesManager mp = new PropertiesManager(object);
		Iterator it = getMetaSets().iterator();
		while (it.hasNext()) {
			MetaSet metaSet = (MetaSet) it.next();
			mp.executeSetFromString(metaSet.getPropertyName(), metaSet.getValue());			
		}		
	}
	
	/** 
	 * @return  Not null
	 */
	public Collection getMetaSets() {
		return metaSets==null?Collections.EMPTY_LIST:metaSets;
	}
	
	public Collection getMetaSetsWithoutValue() {		
		Collection result = new ArrayList();
		Iterator it = getMetaSets().iterator();
		while (it.hasNext()) {
			MetaSet set = (MetaSet) it.next();
			if (!set.hasValue()) {
				result.add(set);
			}
		}
		return result;
	}
	
	public boolean containsMetaSets() {
		return metaSets != null;
	}
	
	public boolean containsMetaSetsWithoutValue() {
		return containsMetaSets() && !getMetaSetsWithoutValue().isEmpty();
	}	
	
	public String getPropertyNameForPropertyFrom(String propertyNameFrom) throws ElementNotFoundException {
		if (metaSets==null) 		 
			throw new ElementNotFoundException("not_value_from_other_property", propertyNameFrom);
		Iterator it = metaSets.iterator();
		while (it.hasNext()) {
			MetaSet metaSet = (MetaSet) it.next();
			if (metaSet.getPropertyNameFrom().equals(propertyNameFrom)) {
				return metaSet.getPropertyName();
			}
		}	
		throw new ElementNotFoundException("not_value_from_other_property", propertyNameFrom);
	}

	/**
	 * Depends on other properties.
	 * 
	 * @since 5.1
	 */
	public boolean isDependent() {
		boolean notDependent = true;
		if (!containsMetaSets()) notDependent = true; 
		else if (containsMetaSetsWithoutValue()) notDependent = false;
		return !notDependent;
	}


}
