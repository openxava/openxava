package org.openxava.model.meta;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.mapping.*;
import org.openxava.util.*;

/**
 * Aggregate whose implementation is a JavaBean. <p>
 * 
 * @author: Javier Paniza
 */

public class MetaAggregateForReference extends MetaAggregate {
	
	private static Log log = LogFactory.getLog(MetaAggregateForReference.class);
	
	private java.lang.String beanClass;	
	private Map persistentPropertiesReferencesMap;
		
	public java.lang.String getBeanClass() throws XavaException {
		if (Is.emptyString(beanClass)) {
			String packageName = getMetaComponent().getPackageName();
			beanClass =  packageName + "." + getName();
		}
		return beanClass;
	}
	
	public void setBeanClass(java.lang.String newClass) {
		beanClass = newClass;
	}
		
	public Class getPropertiesClass() throws XavaException {
		try {
			return Class.forName(getBeanClass());
		} 
		catch (ClassNotFoundException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("no_class_for_model", getBeanClass(), getName());
		}
	}

	public ModelMapping getMapping() throws XavaException { 
		throw new XavaException("aggregate_bean_no_mapping", getName(), getMetaComponent().getName());
	}
		
	public Collection getMetaPropertiesPersistents(MetaReference ref) throws XavaException {
		Collection result = (Collection) getPersistentPropertiesReferencesMap().get(ref);		
		if (result != null) {
			return result;
		}
		result = new ArrayList();
		Iterator it = getMembersNames().iterator(); // member names to keep order
		
		ModelMapping mapping = ref.getMetaModel().getMapping();
		String prefix = ref.getName() + "_";
		while (it.hasNext()) {
			String name = (String) it.next();
			if (!containsMetaProperty(name)) continue;			
			if (mapping.hasPropertyMapping(prefix + name)) {
				MetaProperty p = (MetaProperty) getMetaProperty(name);
				result.add(p);
			}
		}					
		result = Collections.unmodifiableCollection(result);
		getPersistentPropertiesReferencesMap().put(ref, result);
		return result;
	}
	
	private Map getPersistentPropertiesReferencesMap() {
		if (persistentPropertiesReferencesMap == null) {
			persistentPropertiesReferencesMap = new HashMap();
		}
		return persistentPropertiesReferencesMap;
	}

	
	public Collection getPersistentsPropertiesNames(MetaReference ref) throws XavaException {
		Iterator it = getMetaPropertiesPersistents(ref).iterator();
		Collection result = new ArrayList();
		while (it.hasNext()) {
			MetaProperty pr = (MetaProperty) it.next();
			result.add(pr.getName());			
		}
		return result;
	}
	
}